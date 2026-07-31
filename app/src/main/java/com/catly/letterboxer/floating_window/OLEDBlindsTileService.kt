package com.catly.letterboxer.floating_window

import android.app.NotificationManager
import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.catly.letterboxer.R


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
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
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
        // getQsTile() is null whenever the tile is not currently bound, which the system can do
        // immediately after onClick.
        val tile = qsTile ?: return
        tile.state = Tile.STATE_ACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.ic_oledsaveron)
        tile.updateTile()
    }

    fun setOffIcon(){
        val tile = qsTile ?: return
        tile.state = Tile.STATE_INACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.ic_oledsaveronoff)
        tile.updateTile()
    }
}