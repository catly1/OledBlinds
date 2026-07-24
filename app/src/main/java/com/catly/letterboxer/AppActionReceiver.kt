package com.catly.letterboxer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.catly.letterboxer.floating_window.FloatingWindowService
import com.catly.letterboxer.floating_window.StopService

class AppActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AppActionReceiver", "Received intent: ${intent?.action}") // Log intent action

        if (intent?.action == "com.catly.letterboxer.APP_ACTION") {
            val featureName = intent.getStringExtra("feature_name")
            Log.d("AppActionReceiver", "Feature Name: $featureName") // Log feature name

            if (context != null && featureName != null) {
                when (featureName) {
                    "start_service" -> {
                        Log.d("AppActionReceiver", "Starting FloatingWindowService")
                        val serviceIntent = Intent(context, FloatingWindowService::class.java).apply {
                            action = "letterboxer.start"
                        }
                        context.startForegroundService(serviceIntent)
                    }
                    "stop_service" -> {
                        Log.d("AppActionReceiver", "Starting StopService")
                        val serviceIntent = Intent(context, StopService::class.java).apply {
                            action = "letterboxer.stop"
                        }
                        context.startForegroundService(serviceIntent)
                    }
                    else -> {
                        Log.w("AppActionReceiver", "Unknown feature name: $featureName")
                    }
                }
            } else {
                if (context == null) Log.e("AppActionReceiver", "Context is null")
                if (featureName == null) Log.e("AppActionReceiver", "Feature name is null in intent extras")
            }
        } else {
            Log.w("AppActionReceiver", "Received intent with incorrect action: ${intent?.action}")
        }
    }
}