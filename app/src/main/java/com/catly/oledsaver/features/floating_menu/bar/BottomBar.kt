package com.catly.oledsaver.features.floating_menu.bar

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService
import com.catly.oledsaver.features.utils.Utils

class BottomBar(val floatingMenuService: FloatingMenuService) : BaseMovingBar(floatingMenuService) {
    init {
        TAG = "BottomBar"
        param.width = MATCH_PARENT
        param.height = floatingMenuService.height
        param.gravity = Gravity.BOTTOM
        viewLayout = LayoutInflater.from(context).inflate(R.layout.bottom_bar, null)
        resizeButton = viewLayout.findViewById<ImageButton>(R.id.bottom_resize_button)
        setListeners()
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
//                        stopTopBottomHideRunnables()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        calculatedHeight = (initialHeight - (event.rawY - initialTouchY)).toInt()
                        if (Utils.checkIfValidNumber(calculatedHeight)){
                            param.height = calculatedHeight
                            floatingMenuService.topBar.param.height = calculatedHeight
                            floatingMenuService.topBar.update()
                            update()
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP ->{
                        sharedPreferences.edit().putInt("height", param.height).apply()
//                        hideTopBottomButtons()
                    }
                }

                return false
            }
        })
    }
}