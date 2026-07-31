package com.catly.letterboxer.floating_window

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.catly.letterboxer.R

/**
 * Entry point for the `letterboxer.stop` automation intent.
 *
 * Callers reach it with startForegroundService(), which obliges the service to call startForeground()
 * within a few seconds or the system kills the app with ForegroundServiceDidNotStartInTimeException.
 * The notification is therefore posted before any work, and the channel is created here as well
 * because FloatingWindowService may never have run in this process.
 */
class StopService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        FloatingWindowService.ensureNotificationChannel(this)
        startForeground(NOTIFICATION_ID, buildNotification())
        FloatingWindowService.stopService(this)
        stopSelf()
        return START_NOT_STICKY
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, FloatingWindowService.CHANNEL_ID)
            .setContentTitle("LetterBoxer")
            .setContentText("Stopping LetterBoxer.")
            .setSmallIcon(R.drawable.ic_stat_oledsaver)
            .setSilent(true)
            .build()

    private companion object {
        // Distinct from the id FloatingWindowService uses so the two never overwrite each other.
        const val NOTIFICATION_ID = 2
    }
}
