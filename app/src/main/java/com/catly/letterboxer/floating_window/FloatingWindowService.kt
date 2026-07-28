package com.catly.letterboxer.floating_window

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager


import com.catly.letterboxer.MainActivity
import com.catly.letterboxer.R
import com.catly.letterboxer.floating_window.bar.BaseBar
import com.catly.letterboxer.floating_window.bar.LeftBar
import com.catly.letterboxer.floating_window.bar.RightBar
import com.catly.letterboxer.floating_window.bar.TopBar
import com.catly.letterboxer.floating_window.bar.BottomBar
import com.catly.letterboxer.floating_window.zone.ZoneManager
import com.catly.letterboxer.utils.Utils


class FloatingWindowService : Service() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var windowManager: WindowManager
    lateinit var powerManager: PowerManager
    private lateinit var displayManager: DisplayManager
    lateinit var leftBar: LeftBar
    private lateinit var rightBar: RightBar
    lateinit var topBar: TopBar
    private lateinit var bottomBar: BottomBar
    var width: Int = 0
    var overrideWidthForTopBottom: Int = 0
    var height: Int = 0
    var locked = false
    var override = false
    private var statusBarSize = 0
    var rotation = 0
    var tapBehind = false
    var viewsAttached = false
    private lateinit var zoneManager: ZoneManager

    companion object {
        /**
         * The original channel was created with IMPORTANCE_DEFAULT and an existing channel's
         * importance can never be lowered from code, so a new id is used and the old one deleted.
         */
        const val CHANNEL_ID = "LetterBoxer Service v2"
        private const val LEGACY_CHANNEL_ID = "LetterBoxer Service"

        /** Intent action that puts the custom zones into edit mode when the service starts. */
        const val ACTION_EDIT_ZONES = "letterboxer.zones.edit"

        /**
         * Set when edit mode is what started the service, so finishing the edit puts everything
         * away again instead of leaving bars on screen the user never asked for.
         */
        const val EXTRA_STOP_AFTER_EDIT = "stop_after_edit"

        fun ensureNotificationChannel(context: Context) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            manager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "LetterBoxer Service Channel",
                    // The notification is a permanent status entry, it should never buzz or peek.
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        /** Set only on the watcher's own start, so a manual start can never inherit ownership. */
        const val EXTRA_STARTED_BY_WATCHER = "started_by_watcher"

        fun startService(context: Context, byWatcher: Boolean = false) {
            val startIntent = Intent(context, FloatingWindowService::class.java)
                .putExtra(EXTRA_STARTED_BY_WATCHER, byWatcher)
            context.startForegroundService(startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, FloatingWindowService::class.java)
            context.stopService(stopIntent)
        }

        var isRunning = false

        /**
         * True when [AppWatcherService] started the bars automatically. Only an automatic start may
         * be undone automatically, so leaving a watched app never kills a manually started session.
         *
         * Mirrored into preferences because the system can kill and re-create this service at any
         * time; an in-memory only flag would come back false and strand the bars on forever.
         */
        const val STARTED_BY_WATCHER_KEY = "startedByWatcher"

        var startedByWatcher = false

        fun setStartedByWatcher(context: Context, value: Boolean) {
            startedByWatcher = value
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(STARTED_BY_WATCHER_KEY, value).apply()
        }
    }

    private var flipped = false
    private var stopAfterZoneEdit = false

    private fun onZoneEditingFinished() {
        if (!stopAfterZoneEdit) return
        stopAfterZoneEdit = false
        stopSelf()
    }

    /**
     * Rotation everything on screen was last laid out for. [DisplayListener.onDisplayChanged] also
     * fires for brightness/refresh-rate/HDR changes, which on a variable-refresh-rate panel happens
     * many times per second, so the rotation is compared before touching any window.
     */
    private var lastKnownRotation = INVALID_ROTATION

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences: SharedPreferences, key: String?->
        when (key) {
            "override" -> {
                override = sharedPreferences.getBoolean(key, false)
                    refresh()
            }
            "tapBehind"->{
                tapBehind = sharedPreferences.getBoolean(key, false)
                    handleTapBehind()
            }
            "statusBarSize"->{
                // Read once at start up, so without this the typed value is ignored and then
                // overwritten by the stale one the next time the override handle is dragged.
                statusBarSize = readStatusBarSize()
                refresh()
            }
            Utils.OPACITY_KEY->{
                applyOpacity()
            }
            Utils.ZONE_OPACITY_KEY->{
                if (this::zoneManager.isInitialized) zoneManager.applyOpacity()
            }
            Utils.ZONES_ENABLED_KEY, Utils.ZONES_BLOCK_TOUCHES_KEY->{
                if (this::zoneManager.isInitialized) zoneManager.reload()
            }
        }
    }

    private val displayListener: DisplayListener = object : DisplayListener {
        override fun onDisplayAdded(displayId: Int) {
        }

        override fun onDisplayChanged(displayId: Int) {
            // Cheap checks first, then the rotation query, which is a call into the system.
            if (displayId != Display.DEFAULT_DISPLAY) return
            if (!powerManager.isInteractive) return
            val rotation = currentRotation()
            if (rotation == lastKnownRotation) return
            lastKnownRotation = rotation
            handleRotationChange()
        }

        override fun onDisplayRemoved(displayId: Int) {
        }
    }


    private fun refresh() {
        handleOverrideDimensions()
        if (!viewsAttached) return
        if (flipped) {
            handleLeftRightBarCutoutAdjustment()
        } else {
            topBar.updateWidth(overrideWidthForTopBottom)
            bottomBar.updateWidth(overrideWidthForTopBottom)
        }
    }

    /**
     * Context.getDisplay() throws on a Service context that is not associated with a display, so the
     * default display is asked for by id instead.
     */
    private fun currentRotation(): Int =
        displayManager.getDisplay(Display.DEFAULT_DISPLAY)?.rotation ?: Surface.ROTATION_0

    private fun handleRotationChange() {
        if (override && flipped && viewsAttached) {
            handleLeftRightBarCutoutAdjustment()
        }
        if (this::zoneManager.isInitialized) {
            zoneManager.onRotationChanged()
        }
    }

    private fun handleLeftRightBarCutoutAdjustment(){
        if (!viewsAttached || !flipped) return
        if (override) {
            handleOverrideButton()
            // Queried once: two separate reads could straddle a rotation and leave the bars offset
            // for one orientation while the state says another.
            when (currentRotation()) {
                Surface.ROTATION_90 -> {
                    rotation = 90
                    rightBar.revertX()
                    leftBar.adjustForCutoff(statusBarSize)
                }
                Surface.ROTATION_270 -> {
                    rotation = 270
                    leftBar.revertX()
                    rightBar.adjustForCutoff(statusBarSize)
                }
                // Upright: no cutout to overlap, and any offset left over from landscape has to go.
                else -> {
                    rotation = 0
                    leftBar.revertX()
                    rightBar.revertX()
                }
            }
        } else {
            // Otherwise switching the option off would leave a strip of the screen uncovered.
            leftBar.revertX()
            rightBar.revertX()
            rightBar.hideOverrideButton()
            rightBar.disableOverrideButton()
        }
    }

    private fun handleOverrideButton(){
        rightBar.showOverrideButton()
        if (!locked){
            rightBar.enableOverrideButton()
        }
    }

    fun setAndUpdateOffset(offset: Int){
        // A negative offset would be persisted and then doubled into the override bar width.
        statusBarSize = offset.coerceIn(0, Utils.realScreenSize(windowManager).x)
        handleLeftRightBarCutoutAdjustment()
    }

    fun saveOffset(){
        sharedPreferences.edit().putString("statusBarSize", statusBarSize.toString()).apply()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Called first: startForegroundService() gives the service a few seconds to post its
        // notification, and anything below may throw before we get there.
        startForeground(1, buildNotification())

        val savedFlipped = sharedPreferences.getBoolean("isFlipped", false)
        // intent is null when the system restarts a sticky service, so it can't be dereferenced.
        val rightLeft = intent?.getBooleanExtra("right_left", savedFlipped) ?: savedFlipped

        // A null intent is the sticky restart, which has to keep the persisted value; every other
        // start declares its own ownership so a stale "true" cannot survive a process death and
        // make the watcher tear down a session the user started by hand.
        if (intent != null && intent.action != ACTION_EDIT_ZONES) {
            setStartedByWatcher(this, intent.getBooleanExtra(EXTRA_STARTED_BY_WATCHER, false))
        }

        attachBars(rightLeft)

        sharedPreferences.edit().putBoolean("isFlipped", rightLeft).apply()

        setLockState()

        // After the bars, so a zone failure can never stop the letterboxing itself coming up.
        zoneManager.attach()
        // Guarded on isEditing: a repeat tap on "Edit zones" arrives with the extra set to false
        // because the service is now running, and would disarm the flag the first tap set while
        // startEditing() below does nothing.
        if (intent?.action == ACTION_EDIT_ZONES && !zoneManager.isEditing) {
            stopAfterZoneEdit = intent.getBooleanExtra(EXTRA_STOP_AFTER_EDIT, false)
            zoneManager.startEditing()
        }

        return START_STICKY
    }

    /**
     * Attaches the pair of bars for the requested orientation, detaching the other pair first.
     * Without the detach, a second start command (quick tile, Tasker intent, watcher) used to
     * inflate and add a fresh pair of overlay windows while the previous pair stayed on screen and
     * leaked, since only the newest pair is reachable for removal.
     */
    private fun attachBars(rightLeft: Boolean) {
        if (viewsAttached && flipped == rightLeft) return
        detachBars()
        flipped = rightLeft
        try {
            if (rightLeft) leftRightMode() else topDownMode()
        } catch (e: WindowManager.BadTokenException) {
            // The overlay permission was revoked while the service was running. Without this the
            // exception escapes onStartCommand and START_STICKY turns it into a crash loop.
            viewsAttached = true
            detachBars()
            stopSelf()
            return
        }
        viewsAttached = true
        if (rightLeft) handleLeftRightBarCutoutAdjustment()
        // The bars were just added on top of the zones, so the zones go back above them.
        if (this::zoneManager.isInitialized) zoneManager.bringToFront()
    }

    private fun detachBars() {
        if (!viewsAttached) return
        if (flipped) removeLeftRight() else removeTopBottom()
        viewsAttached = false
    }

    private fun buildNotification(): android.app.Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LetterBoxer")
            .setContentText("LetterBoxer is running.")
            .setSmallIcon(R.drawable.ic_stat_oledsaver)
            .setContentIntent(pendingIntent)
            .setSilent(true)
            .build()
    }

    private fun getPrefValuesAndSystemServices(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        override = sharedPreferences.getBoolean("override", false)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
        locked = sharedPreferences.getBoolean("isLocked", false)
        statusBarSize = readStatusBarSize()
        tapBehind = sharedPreferences.getBoolean("tapBehind", false)
        startedByWatcher = sharedPreferences.getBoolean(STARTED_BY_WATCHER_KEY, false)
    }

    /** Typed by hand in a text preference, so it is not guaranteed to be a number. */
    private fun readStatusBarSize(): Int =
        sharedPreferences.getString("statusBarSize", "92")?.trim()?.toIntOrNull()?.coerceAtLeast(0) ?: 92

    override fun onCreate() {
        super.onCreate()
        getPrefValuesAndSystemServices()
        ensureNotificationChannel(this)
        setWidthHeightValues()
        handleOverrideDimensions()
        zoneManager = ZoneManager(this) { onZoneEditingFinished() }
        lastKnownRotation = currentRotation()
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
        displayManager.registerDisplayListener(displayListener, Handler(Looper.getMainLooper()))
        isRunning = true
    }

    /**
     * A second rotation signal. The display listener is the one that distinguishes 90 from 270,
     * which Configuration cannot, but this fires in cases where a background process is not sent
     * display callbacks.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // No dedup here on purpose. The display callback compares a rotation read from
        // DisplayManager, while the zones read orientation from the Configuration this callback
        // delivers; if the display callback ran first against a stale Configuration it would claim
        // the rotation and leave the zones laid out for the old orientation for the whole session.
        // handleRotationChange is idempotent, and this also fires for locale and font size changes.
        lastKnownRotation = currentRotation()
        handleRotationChange()
    }

    private fun setLockState(){
        if (locked){
            lockButtons()
        }
    }

    private fun handleOverrideDimensions(){
        overrideWidthForTopBottom = if (override){
            Utils.realScreenSize(windowManager).x + statusBarSize * 2
        } else {
            MATCH_PARENT
        }
    }

    private fun setWidthHeightValues(){
        width = sharedPreferences.getInt("width", 200)
        width = if (checkIfValidNumber(width)){
            width
        } else {
            200
        }
        height = sharedPreferences.getInt("height", 200)
        height = if (checkIfValidNumber(height)){
            height
        } else {
            200
        }
    }

    private fun topDownMode(){
        bottomBar = BottomBar(this)
        topBar = TopBar(this)
        bottomBar.attach()
        topBar.attach()
    }

    private fun leftRightMode(){
        rightBar = RightBar(this)
        leftBar = LeftBar(this)
        leftBar.attach()
        rightBar.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::zoneManager.isInitialized) zoneManager.detach()
        detachBars()
        displayManager.unregisterDisplayListener(displayListener)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
        isRunning = false
        setStartedByWatcher(this, false)
    }

    // Guarded because a failure part way through creating a pair leaves one of them unassigned.
    private fun removeTopBottom(){
        if (this::bottomBar.isInitialized) bottomBar.remove()
        if (this::topBar.isInitialized) topBar.remove()
    }

    private fun removeLeftRight(){
        if (this::leftBar.isInitialized) leftBar.remove()
        if (this::rightBar.isInitialized) rightBar.remove()
    }

    /** The pair of bars currently on screen, empty while nothing is attached. */
    private fun attachedBars(): List<BaseBar> = when {
        !viewsAttached -> emptyList()
        flipped -> listOf(leftBar, rightBar)
        else -> listOf(topBar, bottomBar)
    }

    private fun handleTapBehind(){
        attachedBars().forEach { it.handleTapBehindAndUpdate() }
    }

    private fun applyOpacity(){
        attachedBars().forEach { it.applyOpacityAndUpdate() }
    }

    fun lockButtons(){
        if (!viewsAttached) return
        if (flipped){
            leftBar.lockButtons()
            rightBar.lockButtons()
        } else {
            bottomBar.lockButtons()
            topBar.lockButtons()
        }
    }

    fun unlockButtons(){
        if (!viewsAttached) return
        if (flipped){
            leftBar.unlockButtons()
            rightBar.unlockButtons()
        } else {
            topBar.unlockButtons()
            bottomBar.unlockButtons()
        }
    }


    fun rotate(){
        setWidthHeightValues()
        val newFlipped = !flipped
        attachBars(newFlipped)
        PreferenceManager.getDefaultSharedPreferences(this).edit()
            .putBoolean("isFlipped", newFlipped).apply()
    }

    private fun checkIfValidNumber(num: Int) : Boolean = Utils.checkIfValidNumber(num)



    fun showButtons() {
        if (flipped){
            showLeftRightButtons()
            hideLeftRightButtons()
        } else {
            showTopBottomButtons()
            hideTopBottomButtons()
        }
    }

    fun showLeftRightButtons(){
        leftBar.showButtons()
        rightBar.showButtons()
    }

    fun hideLeftRightButtons(){
        leftBar.hideButtons()
        rightBar.hideButtons()
    }

    fun showTopBottomButtons(){
        topBar.showButtons()
        bottomBar.showButtons()
    }

    fun hideTopBottomButtons(){
        topBar.hideButtons()
        bottomBar.hideButtons()
    }
}

private const val INVALID_ROTATION = -1
