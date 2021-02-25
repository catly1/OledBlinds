package com.catly.oledsaver.features.floating_window.bar

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService
import com.catly.oledsaver.features.utils.Utils


class RightBar(val floatingWindowService: FloatingWindowService) : BaseMovingBar(floatingWindowService) {
    init {
        TAG = "RightBar"
        param.width = floatingWindowService.width
        param.height = MATCH_PARENT
        param.gravity = Gravity.RIGHT
        viewLayout = LayoutInflater.from(context).inflate(R.layout.right_bar, null)
        resizeButton = viewLayout.findViewById(R.id.right_resize_button)
        overrideButton = viewLayout.findViewById(R.id.right_override_button)
        buttonsGroup = viewLayout.findViewById<View>(R.id.right_bar_buttons)
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
            var initialX: Int = 0
            var initialTouchX: Float = 0.toFloat()
            var initialWidth: Int = 0
            var calculatedWidth = 0
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = param.x
                        initialWidth = param.width
                        initialTouchX = event.rawX
                        floatingWindowService.showLeftRightButtons()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        calculatedWidth = (initialWidth - (event.rawX - initialTouchX)).toInt()
                        if (Utils.checkIfValidNumber(calculatedWidth)) {
                            floatingWindowService.leftBar.param.width = calculatedWidth
                            param.width = calculatedWidth
                            floatingWindowService.leftBar.update()
                            update()
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP ->{
                        sharedPreferences.edit().putInt("width", param.width).apply()
                        floatingWindowService.hideLeftRightButtons()
                    }
                }

                return false
            }
        })

        overrideButton.setOnTouchListener(object : View.OnTouchListener {
                var initialX: Int = 0
                var initialTouchX: Float = 0.toFloat()
                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = param.x
                            initialTouchX = event.rawX
                            floatingWindowService.showLeftRightButtons()
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val calculatedX = (initialX - (event.rawX - initialTouchX)).toInt()
//                            param.x = (initialX - (event.rawX - initialTouchX)).toInt()
                            when (floatingWindowService.rotation){
                                90 -> {floatingWindowService.setAndUpdateOffset(calculatedX)}
                                270 -> {floatingWindowService.setAndUpdateOffset(-calculatedX)}
                            }
//                            println("left: " + leftParam.x)
//                            println("right: " + rightParam.x)
                            return true
                        }
                        MotionEvent.ACTION_UP ->{
                            floatingWindowService.saveOffset()
                            floatingWindowService.hideLeftRightButtons()
                        }
                    }

                    return false
                }
        })
    }
}
