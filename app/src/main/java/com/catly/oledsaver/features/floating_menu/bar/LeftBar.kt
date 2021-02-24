package com.catly.oledsaver.features.floating_menu.bar

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService

class LeftBar(floatingMenuService: FloatingMenuService): BaseButtonsBar(floatingMenuService) {
    init {
        TAG = "LeftBar"
        param.height = MATCH_PARENT
        param.gravity = Gravity.LEFT
        viewLayout = LayoutInflater.from(context).inflate(R.layout.left_bar,null)
        closeButton = viewLayout.findViewById<ImageButton>(R.id.top_close_button)
        rotateButton = viewLayout.findViewById<ImageButton>(R.id.top_rotate_button)
        lockButton = viewLayout.findViewById<ImageButton>(R.id.top_lock_button)
        setListeners()
    }
}