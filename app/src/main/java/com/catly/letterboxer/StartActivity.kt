package com.catly.letterboxer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startForegroundService
import com.catly.letterboxer.floating_window.FloatingWindowService

class StartActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Launch your service here
        val intent = Intent(this, FloatingWindowService::class.java)
        startForegroundService(this, intent)
        finish()
    }
}