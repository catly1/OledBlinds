package com.catly.oledsaver.features.floating_window.bar

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService

class TopBar(floatingWindowService: FloatingWindowService): BaseButtonsBar(floatingWindowService) {
    init {
        TAG = "TopBar"
        param.width = floatingWindowService.overrideWidthForTopBottom
        param.height = floatingWindowService.height
        param.gravity = Gravity.TOP
        viewLayout = LayoutInflater.from(context).inflate(R.layout.top_bar,null)
        closeButton = viewLayout.findViewById(R.id.top_close_button)
        rotateButton = viewLayout.findViewById(R.id.top_rotate_button)
        lockButton = viewLayout.findViewById(R.id.top_lock_button)
        buttonsGroup = viewLayout.findViewById<View>(R.id.top_bar_buttons)
        hideRunnable = Runnable {
            buttonsGroup.visibility = View.GONE
        }
        setListeners()
        setLockIconFromPrefs(lockButton)
        hideButtons()
        handleBarVisibility(floatingWindowService)
    }
}