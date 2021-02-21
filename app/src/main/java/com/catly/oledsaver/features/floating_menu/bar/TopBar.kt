package com.catly.oledsaver.features.floating_menu.bar

import android.view.LayoutInflater
import android.widget.ImageButton
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService

class TopBar(floatingMenuService: FloatingMenuService): BaseButtonsBar(floatingMenuService) {
    init {
        TAG = "TopBar"
        viewLayout = LayoutInflater.from(context).inflate(R.layout.top_bar,null)
        closeButton = viewLayout.findViewById<ImageButton>(R.id.top_close_button)
        rotateButton = viewLayout.findViewById<ImageButton>(R.id.top_rotate_button)
        lockButton = viewLayout.findViewById<ImageButton>(R.id.top_lock_button)
    }
}