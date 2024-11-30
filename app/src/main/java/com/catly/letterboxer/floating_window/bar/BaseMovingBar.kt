package com.catly.letterboxer.floating_window.bar

import android.widget.ImageButton
import com.catly.letterboxer.R

import com.catly.letterboxer.floating_window.FloatingWindowService

open class BaseMovingBar(floatingWindowService: FloatingWindowService): BaseBar(floatingWindowService) {
    lateinit var resizeButton: ImageButton
    lateinit var overrideButton: ImageButton

    override fun lockButtons(){
        resizeButton.isEnabled = false
        super.lockButtons()
    }

    open fun unlockButtons(){
        resizeButton.isEnabled = true
    }

    fun hideOverrideButton(){
        overrideButton.setImageResource(R.color.black)
    }

    fun showOverrideButton(){
        overrideButton.setImageResource(R.drawable.baseline_api_white_24dp)
    }

    fun disableOverrideButton(){
        overrideButton.isEnabled = false
    }

    fun enableOverrideButton(){
        overrideButton.isEnabled = true
    }
}