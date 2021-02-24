package com.catly.oledsaver.features.floating_menu.bar

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.preference.PreferenceManager
import com.catly.oledsaver.features.floating_menu.FloatingMenuService
import com.catly.oledsaver.features.utils.Utils.checkIfValidNumber

open class BaseMovingBar(floatingMenuService: FloatingMenuService): BaseBar(floatingMenuService) {
    lateinit var resizeButton: ImageButton

    fun lockButtons(){
        resizeButton.isEnabled = false
    }

    fun unlockButtons(){
        resizeButton.isEnabled = true
    }
}