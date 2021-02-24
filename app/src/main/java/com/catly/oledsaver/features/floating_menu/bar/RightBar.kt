package com.catly.oledsaver.features.floating_menu.bar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService
import com.catly.oledsaver.features.utils.Utils
import kotlinx.android.synthetic.main.right_bar.view.*


class RightBar(val floatingMenuService: FloatingMenuService) : BaseMovingBar(floatingMenuService) {
    init {
        TAG = "RightBar"
        viewLayout = LayoutInflater.from(context).inflate(R.layout.right_bar, null)
        resizeButton = viewLayout.findViewById<ImageButton>(R.id.right_resize_button)
        setListeners()
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
//                        stopLeftRightHideRunnables()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        calculatedWidth = (initialWidth - (event.rawX - initialTouchX)).toInt()
                        if (Utils.checkIfValidNumber(calculatedWidth)) {
                            floatingMenuService.leftBar.param.width = calculatedWidth
                            param.width = calculatedWidth
                            floatingMenuService.leftBar.update()
                            update()
                        }
//                            println("left: " + leftParam.x)
//                            println("right: " + rightParam.x)
                        return true
                    }
                    MotionEvent.ACTION_UP ->{
                        sharedPreferences.edit().putInt("width", param.width).apply()
//                        hideLeftRightButtons()
                    }
                }

                return false
            }
        })
    }
}