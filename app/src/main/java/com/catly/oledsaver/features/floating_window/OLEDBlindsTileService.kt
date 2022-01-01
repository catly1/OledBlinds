package com.catly.oledsaver.features.floating_window

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R

class OLEDBlindsTileService : TileService() {

    override fun onClick() {
        super.onClick()
        if (!FloatingWindowService.isRunning) {
            FloatingWindowService.startService(this)
            setOnIcon()
        } else {
            FloatingWindowService.stopService(this)
            setOffIcon()
        }

        val closeNotificationPanelIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        this.sendBroadcast(closeNotificationPanelIntent)
    }

    override fun onStartListening() {
        super.onStartListening()

        if (FloatingWindowService.isRunning) {
            setOnIcon()
        } else {
            setOffIcon()
        }
    }

    fun setOnIcon(){
        val tile = qsTile
        tile.state = Tile.STATE_ACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.ic_oledsaveron)
        tile.updateTile()
    }

    fun setOffIcon(){
        val tile = qsTile
        tile.state = Tile.STATE_INACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.ic_oledsaveronoff)
        tile.updateTile()
    }
}