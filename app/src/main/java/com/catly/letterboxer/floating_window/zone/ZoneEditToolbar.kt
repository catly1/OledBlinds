package com.catly.letterboxer.floating_window.zone

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import com.catly.letterboxer.R

/**
 * The floating controls shown while zones are being edited. It is a separate overlay window so it
 * stays reachable no matter which app is in the foreground -- the whole point of edit mode is to
 * place zones on top of the real app, not on top of a settings screen.
 */
class ZoneEditToolbar(
    context: Context,
    private val windowManager: WindowManager,
    private val callbacks: Callbacks
) {

    interface Callbacks {
        fun onAddZone()
        fun onDoneEditing()
    }

    private val param = WindowManager.LayoutParams(
        MATCH_PARENT,
        WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.BOTTOM
    }

    private val viewLayout: View =
        LayoutInflater.from(context).inflate(R.layout.zone_edit_toolbar, null)

    private var attached = false
    private var atBottom = true

    init {
        viewLayout.findViewById<ImageButton>(R.id.zone_add_button).setOnClickListener {
            callbacks.onAddZone()
        }
        viewLayout.findViewById<ImageButton>(R.id.zone_flip_button).setOnClickListener {
            flip()
        }
        viewLayout.findViewById<TextView>(R.id.zone_done_button).setOnClickListener {
            callbacks.onDoneEditing()
        }
    }

    fun attach() {
        if (attached) return
        windowManager.addView(viewLayout, param)
        attached = true
    }

    fun remove() {
        if (!attached) return
        attached = false
        // Same reason as ZoneOverlay.remove: View.isAttachedToWindow is not true until the first
        // frame traversal, so it cannot be used to decide whether a window needs taking down.
        try {
            windowManager.removeView(viewLayout)
        } catch (e: IllegalArgumentException) {
            // Already gone with its window token.
        }
    }

    /** Re-adds the window so it sits above zones that were created after it. */
    fun bringToFront() {
        if (!attached) return
        remove()
        attach()
    }

    /** Lets the user get the toolbar out of the way of a zone placed at that edge of the screen. */
    private fun flip() {
        atBottom = !atBottom
        param.gravity = if (atBottom) Gravity.BOTTOM else Gravity.TOP
        viewLayout.findViewById<ImageButton>(R.id.zone_flip_button)
            .setImageResource(
                if (atBottom) R.drawable.baseline_keyboard_arrow_up_white_24dp
                else R.drawable.baseline_keyboard_arrow_down_white_24dp
            )
        if (attached) {
            windowManager.updateViewLayout(viewLayout, param)
        }
    }
}
