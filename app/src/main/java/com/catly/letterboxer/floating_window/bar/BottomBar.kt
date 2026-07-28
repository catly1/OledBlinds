package com.catly.letterboxer.floating_window.bar

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import com.catly.letterboxer.R

import com.catly.letterboxer.floating_window.FloatingWindowService
import com.catly.letterboxer.utils.Utils

class BottomBar(val floatingWindowService: FloatingWindowService) : BaseMovingBar(floatingWindowService) {
    init {
        TAG = "BottomBar"
        param.width = floatingWindowService.overrideWidthForTopBottom
        param.height = floatingWindowService.height
        param.gravity = Gravity.BOTTOM
        viewLayout = LayoutInflater.from(context).inflate(R.layout.bottom_bar, null)
        resizeButton = viewLayout.findViewById<ImageButton>(R.id.bottom_resize_button)
        buttonsGroup = viewLayout.findViewById<View>(R.id.bottom_bar_buttons)
        hideRunnable = Runnable {
            buttonsGroup.visibility = View.GONE
        }
        setListeners()
        hideButtons()
        handleBarVisibility(floatingWindowService)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setListeners(){
        // Cached once: querying the display on every move event would be a system call per frame.
        val maxHeight = Utils.realScreenSize(windowManager).y

        resizeButton.setOnTouchListener(object : View.OnTouchListener {
            var initialTouchY: Float = 0.toFloat()
            var initialHeight: Int = 0
            var calculatedHeight = 0

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialHeight = param.height
                        initialTouchY = event.rawY
                        floatingWindowService.showTopBottomButtons()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        calculatedHeight = Utils.clampBarSize(
                            (initialHeight - (event.rawY - initialTouchY)).toInt(),
                            maxHeight
                        )
                        param.height = calculatedHeight
                        floatingWindowService.topBar.param.height = calculatedHeight
                        floatingWindowService.topBar.update()
                        update()
                        return true
                    }
                    // A cancel is routinely delivered when the window moves out from under the
                    // finger. Without it the controls would stay lit and the size unsaved.
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->{
                        sharedPreferences.edit().putInt("height", param.height).apply()
                        floatingWindowService.hideTopBottomButtons()
                    }
                }

                return false
            }
        })
    }
}