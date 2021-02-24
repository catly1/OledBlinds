package com.catly.oledsaver.features.floating_menu.bar

import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService

open class BaseButtonsBar(val floatingMenuService: FloatingMenuService): BaseBar(floatingMenuService) {
    lateinit var closeButton: ImageButton
    lateinit var lockButton: ImageButton
    lateinit var rotateButton: ImageButton
    private val lockedIcon = R.drawable.baseline_lock_white_24dp
    private val unlockedIcon = R.drawable.baseline_lock_open_white_24dp

    fun setListeners(){
        closeButton.setOnClickListener {
            floatingMenuService.stopSelf()
        }
        rotateButton.setOnClickListener {
            floatingMenuService.rotate()
        }
        lockButton.setOnClickListener {
            handleLockIcon(it as ImageButton)
        }
    }

    private fun handleLockIcon(imageButton: ImageButton) {
        floatingMenuService.locked = if (floatingMenuService.locked) {
            imageButton.setImageResource(unlockedIcon)
            floatingMenuService.unlockButtons()
            sharedPreferences.edit().putBoolean("isLocked", false).apply()
            false
        } else {
            imageButton.setImageResource(lockedIcon)
            floatingMenuService.lockButtons()
            sharedPreferences.edit().putBoolean("isLocked", true).apply()
            true
        }
    }

    fun setLockIconFromPrefs(imageButton: ImageButton){
        if (floatingMenuService.locked) {
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