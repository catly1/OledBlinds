package com.catly.letterboxer.floating_window

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.catly.letterboxer.floating_window.FloatingWindowService
import com.catly.oledblinds.R

class OLEDBlindsTileService : TileService() {

    override fun onClick() {
        super.onClick()
        if (!FloatingWindowService.Companion.isRunning) {
            FloatingWindowService.Companion.startService(this)
            setOnIcon()
        } else {
            FloatingWindowService.Companion.stopService(this)
            setOffIcon()
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
            val closeNotificationPanelIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            this.sendBroadcast(closeNotificationPanelIntent)
        }
    }

    override fun onStartListening() {
        super.onStartListening()

        if (FloatingWindowService.Companion.isRunning) {
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