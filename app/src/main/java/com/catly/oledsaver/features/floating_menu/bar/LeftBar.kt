package com.catly.oledsaver.features.floating_menu.bar

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService

class LeftBar(floatingMenuService: FloatingMenuService): BaseButtonsBar(floatingMenuService) {
    init {
        TAG = "LeftBar"
        param.height = MATCH_PARENT
        param.width = floatingMenuService.width
        param.gravity = Gravity.LEFT
        viewLayout = LayoutInflater.from(context).inflate(R.layout.left_bar,null)
        closeButton = viewLayout.findViewById(R.id.left_close_button)
        rotateButton = viewLayout.findViewById(R.id.left_rotate_button)
        lockButton = viewLayout.findViewById(R.id.left_lock_button)
        hideRunnable = Runnable {
            viewLayout.findViewById<View>(R.id.left_bar_buttons).visibility = View.GONE
        }
        setListeners()
        setLockIconFromPrefs(lockButton)
        hideButtons()
    }
}