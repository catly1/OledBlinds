package com.catly.letterboxer.floating_window

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import androidx.preference.PreferenceManager

/**
 * Turns the bars on while one of the watched apps is in the foreground and off again once the user
 * leaves it.
 *
 * An accessibility service is used rather than polling [android.app.usage.UsageStatsManager]: the
 * system pushes a single event per window change, so nothing runs while the foreground app stays
 * put. The service must receive events from every package, not just the watched ones, otherwise
 * there is no event to tell us the user has left.
 */
class AppWatcherService : AccessibilityService() {

    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            if (key == PREF_KEY && !preferences.getBoolean(key, false)) {
                handler.removeCallbacks(stopBarsRunnable)
                if (FloatingWindowService.isRunning && FloatingWindowService.startedByWatcher) {
                    FloatingWindowService.stopService(this)
                    FloatingWindowService.setStartedByWatcher(this, false)
                }
            }
        }

    private val stopBarsRunnable = Runnable {
        if (FloatingWindowService.isRunning && FloatingWindowService.startedByWatcher) {
            FloatingWindowService.stopService(this)
            FloatingWindowService.setStartedByWatcher(this, false)
        }
    }

    companion object {
        const val PREF_KEY = "autoTikTok"
        const val WATCHED_PACKAGES_KEY = "watchedPackages"

        /** Every TikTok/Douyin build that ships under its own package name. */
        val DEFAULT_WATCHED_PACKAGES = setOf(
            "com.zhiliaoapp.musically",       // TikTok
            "com.zhiliaoapp.musically.go",    // TikTok Lite
            "com.ss.android.ugc.trill",       // TikTok (alternative global build)
            "com.ss.android.ugc.aweme",       // Douyin
            "com.ss.android.ugc.aweme.lite"   // Douyin Lite
        )

        /**
         * Windows that are drawn over the current app instead of replacing it. Treating them as an
         * app switch would drop the bars every time the notification shade is pulled down.
         */
        private val OVERLAY_PACKAGES = setOf(
            "com.android.systemui",
            "android"
        )

        /**
         * Leaving a watched app is applied with a delay so that a window that only flashes past --
         * the launcher during a gesture, a permission dialog, a share sheet -- does not toggle the
         * bars off and straight back on.
         */
        private const val LEAVE_DELAY_MS = 500L

        /**
         * Returns only the watched packages that are actually installed on the user's device when
         * context is provided, avoiding inflated package counts.
         */
        fun watchedPackages(sharedPreferences: SharedPreferences, context: Context? = null): Set<String> {
            val stored = sharedPreferences.getStringSet(WATCHED_PACKAGES_KEY, null)
            val baseSet = stored ?: DEFAULT_WATCHED_PACKAGES
            if (context != null) {
                val pm = context.packageManager
                val installed = baseSet.filter { pkg ->
                    try {
                        pm.getPackageInfo(pkg, 0)
                        true
                    } catch (e: Exception) {
                        false
                    }
                }.toSet()
                if (installed.isNotEmpty()) return installed
            }
            return HashSet(baseSet)
        }

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val expected = ComponentName(context, AppWatcherService::class.java)
            val enabled = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return enabled.split(':')
                .mapNotNull { ComponentName.unflattenFromString(it) }
                .any { it == expected }
        }
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val type = event?.eventType ?: return
        if (type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            type != AccessibilityEvent.TYPE_WINDOWS_CHANGED) return

        val eventPackage = event.packageName?.toString() ?: return
        if (eventPackage == packageName) return
        if (eventPackage in OVERLAY_PACKAGES) return
        if (eventPackage == currentInputMethodPackage()) return

        if (!sharedPreferences.getBoolean(PREF_KEY, false)) return

        val isWatched = eventPackage in watchedPackages(sharedPreferences, this)

        if (isWatched) {
            handler.removeCallbacks(stopBarsRunnable)
            startBars()
        } else {
            handler.removeCallbacks(stopBarsRunnable)
            handler.postDelayed(stopBarsRunnable, LEAVE_DELAY_MS)
        }
    }

    private fun startBars() {
        if (FloatingWindowService.isRunning) return
        if (!Settings.canDrawOverlays(this)) return
        try {
            FloatingWindowService.startService(this, byWatcher = true)
        } catch (e: Exception) {
            FloatingWindowService.setStartedByWatcher(this, false)
        }
    }

    override fun onInterrupt() {
    }

    override fun onUnbind(intent: Intent?): Boolean {
        handler.removeCallbacks(stopBarsRunnable)
        return true
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
        handler.removeCallbacks(stopBarsRunnable)
    }

    /** Stored as a flattened component, e.g. `com.example.ime/.InputService`. */
    private fun currentInputMethodPackage(): String? =
        Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            ?.let { ComponentName.unflattenFromString(it)?.packageName }
}
