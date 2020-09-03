package com.example.oledsaver.features.floating_menu

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.preference.PreferenceManager

class OLEDSaverTileService : TileService() {
    private var status = false
    override fun onClick() {
        super.onClick()
        var serviceIntent = Intent(this, FloatingMenuService::class.java)
        if (status) {
            stopService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        var closeNotificationPanelIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        this.sendBroadcast(closeNotificationPanelIntent)
    }

    override fun onStartListening() {
        super.onStartListening()

        val tile = qsTile

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isActive", false)) {
            //        tile.icon = Icon.createWithResource(this, R.drawable.other_icon)
            tile.state = Tile.STATE_ACTIVE
            status = true
        } else {
            tile.state = Tile.STATE_INACTIVE
            status = false
        }

        tile.updateTile() // you need to call this method to apply changes
    }
}