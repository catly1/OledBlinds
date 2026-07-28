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
    private var lastPackage: String? = null

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            // Otherwise switching the feature on, or adding the app you are currently in to the
            // watched set, does nothing: the package has not changed, so no event ever gets past
            // the debounce and the bars never come up.
            if ((key == PREF_KEY && preferences.getBoolean(key, false)) ||
                key == WATCHED_PACKAGES_KEY
            ) {
                lastPackage = null
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
        private const val LEAVE_DELAY_MS = 600L

        /**
         * The returned set is the live instance held by SharedPreferences and must never be
         * mutated or handed out, hence the copy. An absent key means the defaults; an empty set
         * means the user deselected everything and wants nothing watched.
         */
        fun watchedPackages(sharedPreferences: SharedPreferences): Set<String> {
            val stored = sharedPreferences.getStringSet(WATCHED_PACKAGES_KEY, null)
                ?: return DEFAULT_WATCHED_PACKAGES
            return HashSet(stored)
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
        lastPackage = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val eventPackage = event.packageName?.toString() ?: return
        if (eventPackage == packageName) return
        if (eventPackage in OVERLAY_PACKAGES) return
        // The keyboard raises a window state change of its own without the app behind it going away.
        // Re-read rather than cached: the service lives for days and the default IME can change.
        if (eventPackage == currentInputMethodPackage()) return
        // Apps fire this event on every screen they open; only real app switches are of interest.
        if (eventPackage == lastPackage) return
        lastPackage = eventPackage

        if (!sharedPreferences.getBoolean(PREF_KEY, false)) return

        handler.removeCallbacks(stopBarsRunnable)
        if (eventPackage in watchedPackages(sharedPreferences)) {
            startBars()
        } else {
            handler.postDelayed(stopBarsRunnable, LEAVE_DELAY_MS)
        }
    }

    private fun startBars() {
        if (FloatingWindowService.isRunning) return
        // Starting the overlay service without this permission throws out of its onStartCommand,
        // and START_STICKY would then restart it into the same crash on every app switch.
        if (!Settings.canDrawOverlays(this)) return
        try {
            FloatingWindowService.startService(this, byWatcher = true)
        } catch (e: Exception) {
            // A background foreground-service start can still be refused; do not take the
            // accessibility service down with it.
            FloatingWindowService.setStartedByWatcher(this, false)
        }
    }

    override fun onInterrupt() {
    }

    override fun onUnbind(intent: Intent?): Boolean {
        handler.removeCallbacks(stopBarsRunnable)
        lastPackage = null
        return super.onUnbind(intent)
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
