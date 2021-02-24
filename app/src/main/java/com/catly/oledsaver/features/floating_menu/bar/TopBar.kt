package com.catly.oledsaver.features.floating_menu.bar

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService

class TopBar(floatingMenuService: FloatingMenuService): BaseButtonsBar(floatingMenuService) {
    init {
        TAG = "TopBar"
        param.width = MATCH_PARENT
        param.gravity = Gravity.TOP
        viewLayout = LayoutInflater.from(context).inflate(R.layout.top_bar,null)
        closeButton = viewLayout.findViewById<ImageButton>(R.id.top_close_button)
        rotateButton = viewLayout.findViewById<ImageButton>(R.id.top_rotate_button)
        lockButton = viewLayout.findViewById<ImageButton>(R.id.top_lock_button)
        setListeners()
    }
}