package com.catly.oledsaver.features.floating_window.bar

import android.widget.ImageButton
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService

open class BaseButtonsBar(val floatingWindowService: FloatingWindowService): BaseBar(floatingWindowService) {
    lateinit var closeButton: ImageButton
    lateinit var lockButton: ImageButton
    lateinit var rotateButton: ImageButton
    private val lockedIcon = R.drawable.baseline_lock_white_24dp
    private val unlockedIcon = R.drawable.baseline_lock_open_white_24dp

    fun setListeners(){
        closeButton.setOnClickListener {
            floatingWindowService.stopSelf()
        }
        rotateButton.setOnClickListener {
            floatingWindowService.rotate()
        }
        lockButton.setOnClickListener {
            handleLockIcon(it as ImageButton)
        }
    }

    private fun handleLockIcon(imageButton: ImageButton) {
        floatingWindowService.locked = if (floatingWindowService.locked) {
            imageButton.setImageResource(unlockedIcon)
            floatingWindowService.unlockButtons()
            sharedPreferences.edit().putBoolean("isLocked", false).apply()
            false
        } else {
            imageButton.setImageResource(lockedIcon)
            floatingWindowService.lockButtons()
            sharedPreferences.edit().putBoolean("isLocked", true).apply()
            true
        }
    }

    fun setLockIconFromPrefs(imageButton: ImageButton){
        if (floatingWindowService.locked) {
            imageButton.setImageResource(lockedIcon)
        } else {
            imageButton.setImageResource(unlockedIcon)
        }
    }

    fun lockButtons(){
        closeButton.isEnabled = false
        rotateButton.isEnabled = false
    }

    fun unlockButtons(){
        closeButton.isEnabled = true
        rotateButton.isEnabled = true
    }
}