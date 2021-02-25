package com.catly.oledsaver.features.floating_window.bar

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService
import com.catly.oledsaver.features.utils.Utils

class BottomBar(val floatingWindowService: FloatingWindowService) : BaseMovingBar(floatingWindowService) {
    init {
        TAG = "BottomBar"
        param.width = floatingWindowService.overrideWidth
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
        resizeButton.setOnTouchListener(object : View.OnTouchListener {
            var initialY: Int = 0
            var initialTouchY: Float = 0.toFloat()
            var initialHeight: Int = 0
            var calculatedHeight = 0

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialY = param.y
                        initialHeight = param.height
                        initialTouchY = event.rawY
                        floatingWindowService.showTopBottomButtons()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        calculatedHeight = (initialHeight - (event.rawY - initialTouchY)).toInt()
                        if (Utils.checkIfValidNumber(calculatedHeight)){
                            param.height = calculatedHeight
                            floatingWindowService.topBar.param.height = calculatedHeight
                            floatingWindowService.topBar.update()
                            update()
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP ->{
                        sharedPreferences.edit().putInt("height", param.height).apply()
                        floatingWindowService.hideTopBottomButtons()
                    }
                }

                return false
            }
        })
    }
}