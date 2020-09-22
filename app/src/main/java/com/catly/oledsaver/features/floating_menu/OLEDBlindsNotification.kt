package com.catly.oledsaver.features.floating_menu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.catly.oledsaver.R

class OLEDBlindsNotification(private val service: Service) {

    init{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(service)

            NotificationChannel(
                "OLED Blinds",
                "Service Running",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Service is running in the background"

                setShowBadge(false)
                notificationManager.createNotificationChannel(this)
            }
        }
    }

    fun show(){
        service.startForeground(1, buildNotification().build())
    }

    private fun buildNotification() : NotificationCompat.Builder{
        return NotificationCompat.Builder(service, "service")
            .setOngoing(true)
            .setContentTitle("OLED Blinds")
            .setContentText("Service Running")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationManager.IMPORTANCE_LOW)
    }

    fun hide(){
        service.stopForeground(true)
    }
}