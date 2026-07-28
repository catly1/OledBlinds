package com.catly.letterboxer.floating_window.zone

import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Point
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.catly.letterboxer.R
import com.catly.letterboxer.data.ZoneRepository
import com.catly.letterboxer.data.model.OverlayZone
import com.catly.letterboxer.utils.Utils
import kotlin.math.pow

/**
 * Owns the custom zones: loads them, puts one overlay window on screen per zone that belongs to the
 * current orientation, and drives edit mode.
 *
 * Zones are stored for both orientations at once; only the matching subset is ever attached, because
 * a rectangle placed over a portrait UI means nothing once the screen is landscape.
 */
class ZoneManager(
    private val context: Context,
    /** Invoked when the user leaves edit mode themselves, not when the service is torn down. */
    private val onEditingFinished: () -> Unit = {}
) : ZoneOverlay.Callbacks, ZoneEditToolbar.Callbacks {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    private var allZones: MutableList<OverlayZone> = mutableListOf()
    private val overlays = mutableListOf<ZoneOverlay>()
    private var toolbar: ZoneEditToolbar? = null

    private var screenSize = Point(0, 0)
    private var attachedPortrait = true

    var isAttached = false
        private set
    var isEditing = false
        private set

    fun attach() {
        if (isAttached) return
        isAttached = true
        allZones = ZoneRepository.load(sharedPreferences)
        attachedPortrait = isPortrait()
        screenSize = realScreenSize()
        attachOverlays()
    }

    fun detach() {
        if (!isAttached) return
        stopEditing()
        detachOverlays()
        isAttached = false
    }

    /**
     * Re-adds the zone windows so they sit above bars that were just re-created; overlay windows of
     * the same type are stacked in the order they were added.
     */
    fun bringToFront() {
        if (!isAttached) return
        reattachOverlays()
    }

    fun onRotationChanged() {
        if (!isAttached) return
        val portrait = isPortrait()
        screenSize = realScreenSize()
        if (portrait == attachedPortrait) return
        attachedPortrait = portrait
        reattachOverlays()
    }

    /**
     * Every rebuild goes through here. The overlays are new objects, so an edit session in progress
     * has to be re-applied to them or the user is left with a toolbar and nothing it can drag.
     */
    private fun reattachOverlays() {
        detachOverlays()
        attachOverlays(force = isEditing)
        if (isEditing) {
            val style = style()
            overlays.forEach { it.setEditing(true, style) }
        }
        toolbar?.bringToFront()
    }

    fun applyOpacity() {
        val style = style()
        overlays.forEach { it.applyStyle(style) }
    }

    /** Called when the enabled switch or the stored zone list changes from the settings screen. */
    fun reload() {
        if (!isAttached) return
        allZones = ZoneRepository.load(sharedPreferences)
        reattachOverlays()
    }

    fun startEditing() {
        if (!isAttached || isEditing) return
        isEditing = true
        // Zones are only drawn while enabled, but they still have to be reachable to be edited.
        if (overlays.isEmpty() && zonesForCurrentOrientation().isNotEmpty()) {
            attachOverlays(force = true)
        }
        val style = style()
        overlays.forEach { it.setEditing(true, style) }
        toolbar = ZoneEditToolbar(context, windowManager, this).also { it.attach() }
    }

    fun stopEditing() {
        if (!isEditing) return
        isEditing = false
        val style = style()
        overlays.forEach { it.setEditing(false, style) }
        toolbar?.remove()
        toolbar = null
        // Deliberately no save() here. Every mutation already persists itself, and this also runs
        // from detach() during onDestroy, where it would write the in-memory list back over a
        // Reset that had just cleared it.
        // Leaving edit mode with the feature switched off should put the zones away again.
        if (!zonesEnabled()) {
            detachOverlays()
        }
    }

    // region ZoneEditToolbar.Callbacks

    override fun onAddZone() {
        if (allZones.size >= ZoneRepository.MAX_ZONES) {
            Toast.makeText(
                context,
                context.getString(R.string.zone_limit_reached, ZoneRepository.MAX_ZONES),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val zone = defaultZone()
        val style = style()
        val overlay = ZoneOverlay(context, windowManager, zone, this)
        // Persisted only once the window is really up, so a refused zone does not come back as an
        // invisible entry on the next start.
        if (!attachOverlay(overlay, style)) return
        allZones.add(zone)
        save()
        overlay.setEditing(true, style)
        // The new window was added after the toolbar, so the toolbar has to be lifted back on top.
        toolbar?.bringToFront()
    }

    override fun onDoneEditing() {
        stopEditing()
        onEditingFinished()
    }

    // endregion

    // region ZoneOverlay.Callbacks

    override fun onZoneChanged(zone: OverlayZone) {
        save()
    }

    override fun onZoneDeleted(overlay: ZoneOverlay) {
        overlay.remove()
        overlays.remove(overlay)
        allZones.remove(overlay.zone)
        save()
    }

    override fun screenWidth(): Int = screenSize.x

    override fun screenHeight(): Int = screenSize.y

    // endregion

    private fun attachOverlays(force: Boolean = false) {
        if (!force && !zonesEnabled()) return
        if (!Settings.canDrawOverlays(context)) return
        val style = style()
        zonesForCurrentOrientation().forEach { zone ->
            attachOverlay(ZoneOverlay(context, windowManager, zone, this), style)
        }
    }

    /**
     * Only tracked once the window is really on screen: a half added overlay recorded as attached
     * would be a window nothing can ever take down again.
     */
    private fun attachOverlay(overlay: ZoneOverlay, style: ZoneOverlay.Style): Boolean {
        return try {
            overlay.attach(style)
            overlays.add(overlay)
            true
        } catch (e: WindowManager.BadTokenException) {
            false
        }
    }

    private fun detachOverlays() {
        overlays.forEach { it.remove() }
        overlays.clear()
    }

    private fun zonesForCurrentOrientation(): List<OverlayZone> =
        allZones.filter { it.portrait == attachedPortrait }

    private fun save() {
        ZoneRepository.save(sharedPreferences, allZones)
    }

    private fun zonesEnabled(): Boolean = sharedPreferences.getBoolean(Utils.ZONES_ENABLED_KEY, true)

    /**
     * A zone that is meant to be tapped through has to stay under the system's touch obscuring
     * threshold, otherwise Android 12+ silently swallows every tap aimed at the app underneath.
     *
     * The threshold applies to this app's *combined* opacity over a pixel, accumulated as
     * 1 - product(1 - a) across every one of our overlays covering it, so capping each window on
     * its own is not enough: two zones at 75% that overlap compose to 94% and the touches under the
     * overlap are dropped with nothing logged. The budget is therefore split by how deeply the
     * zones actually stack.
     */
    private fun style(): ZoneOverlay.Style {
        val passThrough = !sharedPreferences.getBoolean(Utils.ZONES_BLOCK_TOUCHES_KEY, false)
        val alpha = Utils.zoneAlpha(sharedPreferences)
        if (!passThrough) return ZoneOverlay.Style(alpha, false)
        val budget = Utils.maxPassThroughAlpha(context)
        val depth = maxOverlapDepth()
        val ceiling = if (depth <= 1) budget else perWindowCeiling(budget, depth)
        return ZoneOverlay.Style(minOf(alpha, ceiling), true)
    }

    /** The per-window alpha whose [depth]-fold composite lands exactly on [budget]. */
    private fun perWindowCeiling(budget: Float, depth: Int): Float =
        (1.0 - (1.0 - budget).pow(1.0 / depth)).toFloat().coerceIn(0f, 1f)

    /**
     * Largest number of zones covering any single pixel. The maximum is always reached at a point
     * whose x is some zone's left edge and whose y is some zone's top edge, so only those corners
     * need testing.
     */
    private fun maxOverlapDepth(): Int {
        val zones = zonesForCurrentOrientation()
        if (zones.size < 2) return zones.size
        var deepest = 1
        for (byX in zones) {
            for (byY in zones) {
                val depth = zones.count { zone ->
                    byX.x >= zone.x && byX.x < zone.x + zone.width &&
                        byY.y >= zone.y && byY.y < zone.y + zone.height
                }
                if (depth > deepest) deepest = depth
            }
        }
        return deepest
    }

    private fun isPortrait(): Boolean =
        context.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE

    /**
     * A new zone is sized and placed for the case this feature exists for: the tall column of static
     * buttons down the right hand edge of a short form video app. Later zones are nudged along so
     * they do not land exactly on top of each other.
     */
    private fun defaultZone(): OverlayZone {
        val width = (screenSize.x * 0.22f).toInt().coerceAtLeast(1)
        val height = (screenSize.y * 0.42f).toInt().coerceAtLeast(1)
        // Tiled, not nudged: a small offset would leave each new zone sitting on top of the last
        // one, and stacked pass-through zones blow the touch obscuring budget.
        val step = width * overlays.size
        val x = (screenSize.x - width - step).coerceAtLeast(0)
        val y = (screenSize.y * 0.3f).toInt()
            .coerceIn(0, (screenSize.y - height).coerceAtLeast(0))
        return OverlayZone(
            id = ZoneRepository.nextId(allZones),
            x = x,
            y = y,
            width = width,
            height = height,
            portrait = attachedPortrait
        )
    }

    private fun realScreenSize(): Point = Utils.realScreenSize(windowManager)
}
