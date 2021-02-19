package com.catly.oledsaver.features.floating_menu.bar

import android.view.LayoutInflater
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService

class TopBar(floatingMenuService: FloatingMenuService): BaseButtonsBar(floatingMenuService) {
    init {
        TAG = "TopBar"
        viewLayout = LayoutInflater.from(context).inflate(R.layout.top_bar,null)
    }
}