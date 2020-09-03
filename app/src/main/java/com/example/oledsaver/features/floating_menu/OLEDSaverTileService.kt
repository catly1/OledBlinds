package com.example.oledsaver.features.floating_menu

import android.content.Intent
import android.service.quicksettings.TileService

class OLEDSaverTileService: TileService() {
    override fun onClick() {
        super.onClick()
        var startServiceIntent = Intent(this, FloatingMenuService::class.java)
        var closeNotificationPanelIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)

//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Called when the user click the tile
        startService(startServiceIntent)
        this.sendBroadcast(closeNotificationPanelIntent)
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        // Do something when the user removes the Tile
    }

    override fun onTileAdded() {
        super.onTileAdded()

        // Do something when the user add the Tile
    }

    override fun onStartListening() {
        super.onStartListening()

        // Called when the Tile becomes visible
    }

    override fun onStopListening() {
        super.onStopListening()

        // Called when the tile is no longer visible
    }
}