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
            floatingMenuService.rotate(rotateButton)
        }
        lockButton.setOnClickListener {

        }
    }

    private fun handleLockIcon(imageButton: ImageButton) {
        if (locked) {
            imageButton.setImageResource(lockedIcon)
            lockButtons()
        } else {
            imageButton.setImageResource(unlockedIcon)
        }

        imageButton.setOnClickListener {
            locked = if (locked) {
                imageButton.setImageResource(unlockedIcon)
                unlockButtons()
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isLocked", false).apply()
                false
            } else {
                imageButton.setImageResource(lockedIcon)
                lockButtons()
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isLocked", true).apply()
                true
            }
        }
    }
}