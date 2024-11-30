package com.catly.letterboxer.floating_window.bar

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.catly.letterboxer.R
import com.catly.letterboxer.floating_window.FloatingWindowService

class LeftBar(floatingWindowService: FloatingWindowService): BaseButtonsBar(floatingWindowService) {
    init {
        TAG = "LeftBar"
        param.height = MATCH_PARENT
        param.width = floatingWindowService.width
        param.gravity = Gravity.LEFT
        viewLayout = LayoutInflater.from(context).inflate(R.layout.left_bar,null)
        closeButton = viewLayout.findViewById(R.id.left_close_button)
        rotateButton = viewLayout.findViewById(R.id.left_rotate_button)
        lockButton = viewLayout.findViewById(R.id.left_lock_button)
        buttonsGroup = viewLayout.findViewById<View>(R.id.left_bar_buttons)
        hideRunnable = Runnable {
            buttonsGroup.visibility = View.GONE
        }
        setListeners()
        setLockIconFromPrefs(lockButton)
        hideButtons()
        handleBarVisibility(floatingWindowService)
    }


}