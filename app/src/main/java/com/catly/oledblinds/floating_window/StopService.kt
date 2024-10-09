package com.catly.oledblinds.floating_window

import android.app.Service
import android.content.Intent
import android.os.IBinder

class StopService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        FloatingWindowService.stopService(this)
        stopSelf()
    }
}