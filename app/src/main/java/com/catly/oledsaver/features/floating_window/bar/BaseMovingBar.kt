package com.catly.oledsaver.features.floating_window.bar

import android.widget.ImageButton
import com.catly.oledsaver.features.floating_window.FloatingWindowService

open class BaseMovingBar(floatingWindowService: FloatingWindowService): BaseBar(floatingWindowService) {
    lateinit var resizeButton: ImageButton
    lateinit var overrideButton: ImageButton

    fun lockButtons(){
        resizeButton.isEnabled = false
    }

    fun unlockButtons(){
        resizeButton.isEnabled = true
    }
}