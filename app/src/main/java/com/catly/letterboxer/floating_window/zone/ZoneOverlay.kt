package com.catly.letterboxer.floating_window.zone

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import com.catly.letterboxer.R
import com.catly.letterboxer.data.model.OverlayZone

/**
 * One overlay window per zone.
 *
 * A single fullscreen overlay would need a whole screen sized buffer and would blend over every
 * pixel; a handful of small windows only cost their own area, which matters for a service that is
 * meant to sit on screen all day.
 *
 * Outside edit mode the window carries FLAG_NOT_TOUCHABLE, so the covered controls still work -- the
 * zone hides the pixels, it does not take the taps.
 */
class ZoneOverlay(
    context: Context,
    private val windowManager: WindowManager,
    val zone: OverlayZone,
    private val callbacks: Callbacks
) {

    /** How a zone should currently be drawn and whether it lets touches reach the app below. */
    data class Style(val alpha: Float, val passThrough: Boolean)

    interface Callbacks {
        fun onZoneChanged(zone: OverlayZone)
        fun onZoneDeleted(overlay: ZoneOverlay)
        fun screenWidth(): Int
        fun screenHeight(): Int
    }

    private val density = context.resources.displayMetrics.density
    // The two handles sit at the top and bottom of the same edge, so height needs room for both
    // while width only has to fit one. A single minimum would forbid zones as narrow as the column
    // of buttons this feature exists to cover.
    private val minWidth = (MIN_WIDTH_DP * density).toInt()
    private val minHeight = (MIN_HEIGHT_DP * density).toInt()

    val param: WindowManager.LayoutParams = WindowManager.LayoutParams(
        zone.width,
        zone.height,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        PASSTHROUGH_FLAGS,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.LEFT
        x = zone.x
        y = zone.y
    }

    private val viewLayout: View = LayoutInflater.from(context).inflate(R.layout.zone_overlay, null)
    private val deleteButton: ImageButton = viewLayout.findViewById(R.id.zone_delete_button)
    private val resizeButton: ImageButton = viewLayout.findViewById(R.id.zone_resize_button)

    private var attached = false
    private var editing = false

    init {
        setListeners()
    }

    fun attach(style: Style) {
        if (attached) return
        clampToScreen()
        applyStyleToParams(style)
        windowManager.addView(viewLayout, param)
        attached = true
    }

    fun remove() {
        if (!attached) return
        attached = false
        // Tracked by hand rather than through View.isAttachedToWindow, which only turns true on the
        // first frame traversal: a window added and taken down in the same frame would be skipped
        // here and left on screen with nothing holding a reference to it.
        try {
            windowManager.removeView(viewLayout)
        } catch (e: IllegalArgumentException) {
            // Already gone with its window token.
        }
    }

    fun applyStyle(style: Style) {
        applyStyleToParams(style)
        update()
    }

    fun setEditing(editing: Boolean, style: Style) {
        this.editing = editing
        if (editing) {
            viewLayout.setBackgroundResource(R.drawable.zone_edit_background)
        } else {
            viewLayout.setBackgroundColor(Color.BLACK)
        }
        val chrome = if (editing) View.VISIBLE else View.GONE
        deleteButton.visibility = chrome
        resizeButton.visibility = chrome
        applyStyleToParams(style)
        update()
    }

    private fun applyStyleToParams(style: Style) {
        // Edit mode has to receive the drags, and keeps the window fully opaque so the frame and
        // handles stay visible; the translucent fill drawable provides the see-through instead.
        param.flags = if (editing || !style.passThrough) EDIT_FLAGS else PASSTHROUGH_FLAGS
        param.alpha = if (editing) 1f else style.alpha
    }

    private fun update() {
        if (!attached) return
        windowManager.updateViewLayout(viewLayout, param)
    }

    /**
     * A zone saved on another device, or before a resolution change, can land off screen where it
     * would be invisible and unreachable in edit mode.
     */
    private fun clampToScreen() {
        val screenWidth = callbacks.screenWidth()
        val screenHeight = callbacks.screenHeight()
        if (screenWidth <= 0 || screenHeight <= 0) return
        param.width = param.width.coerceIn(minWidth.coerceAtMost(screenWidth), screenWidth)
        param.height = param.height.coerceIn(minHeight.coerceAtMost(screenHeight), screenHeight)
        param.x = param.x.coerceIn(0, (screenWidth - param.width).coerceAtLeast(0))
        param.y = param.y.coerceIn(0, (screenHeight - param.height).coerceAtLeast(0))
        writeBack()
    }

    private fun writeBack() {
        zone.x = param.x
        zone.y = param.y
        zone.width = param.width
        zone.height = param.height
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        deleteButton.setOnClickListener {
            callbacks.onZoneDeleted(this)
        }

        // Touching the body moves the zone. The two handles are children, so they get the event
        // first and this listener never sees a drag that started on them.
        viewLayout.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var touchX = 0f
            private var touchY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (!editing) return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = param.x
                        initialY = param.y
                        touchX = event.rawX
                        touchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val maxX = (callbacks.screenWidth() - param.width).coerceAtLeast(0)
                        val maxY = (callbacks.screenHeight() - param.height).coerceAtLeast(0)
                        param.x = (initialX + (event.rawX - touchX)).toInt().coerceIn(0, maxX)
                        param.y = (initialY + (event.rawY - touchY)).toInt().coerceIn(0, maxY)
                        update()
                        return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        writeBack()
                        callbacks.onZoneChanged(zone)
                        return true
                    }
                }
                return false
            }
        })

        resizeButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialWidth = 0
            private var initialHeight = 0
            private var touchX = 0f
            private var touchY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (!editing) return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialWidth = param.width
                        initialHeight = param.height
                        touchX = event.rawX
                        touchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val maxWidth = (callbacks.screenWidth() - param.x).coerceAtLeast(minWidth)
                        val maxHeight = (callbacks.screenHeight() - param.y).coerceAtLeast(minHeight)
                        param.width = (initialWidth + (event.rawX - touchX)).toInt()
                            .coerceIn(minWidth, maxWidth)
                        param.height = (initialHeight + (event.rawY - touchY)).toInt()
                            .coerceIn(minHeight, maxHeight)
                        update()
                        return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        writeBack()
                        callbacks.onZoneChanged(zone)
                        return true
                    }
                }
                return false
            }
        })
    }

    private companion object {
        const val MIN_WIDTH_DP = 48
        const val MIN_HEIGHT_DP = 96

        const val PASSTHROUGH_FLAGS = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        const val EDIT_FLAGS = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    }
}
