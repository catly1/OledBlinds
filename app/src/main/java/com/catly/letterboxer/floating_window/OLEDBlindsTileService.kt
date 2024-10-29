package com.catly.oledblinds.floating_window

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.catly.oledblinds.R

class OLEDBlindsTileService : TileService() {

    override fun onClick() {
        super.onClick()
        if (!com.catly.oledblinds.floating_window.FloatingWindowService.Companion.isRunning) {
            com.catly.oledblinds.floating_window.FloatingWindowService.Companion.startService(this)
            setOnIcon()
        } else {
            com.catly.oledblinds.floating_window.FloatingWindowService.Companion.stopService(this)
            setOffIcon()
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
            val closeNotificationPanelIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            this.sendBroadcast(closeNotificationPanelIntent)
        }
    }

    override fun onStartListening() {
        super.onStartListening()

        if (com.catly.oledblinds.floating_window.FloatingWindowService.Companion.isRunning) {
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