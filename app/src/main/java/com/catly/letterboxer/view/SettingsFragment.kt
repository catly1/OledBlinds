package com.catly.letterboxer.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import com.catly.letterboxer.floating_window.AppWatcherService
import com.catly.letterboxer.floating_window.FloatingWindowService
import com.catly.letterboxer.R
import com.catly.letterboxer.data.ZoneRepository
import com.catly.letterboxer.utils.Utils

class SettingsFragment : PreferenceFragmentCompat() {
    private var currentDialog : MessageDialogFragment? = null
    private var awaitingAccessibility = false

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<Preference>("reset")?.setOnPreferenceClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            prefs.edit().clear().apply()

            if (FloatingWindowService.isRunning) {
                FloatingWindowService.stopService(requireContext())
            }

            findPreference<SeekBarPreference>(Utils.OPACITY_KEY)?.value = Utils.DEFAULT_OPACITY
            findPreference<SeekBarPreference>(Utils.ZONE_OPACITY_KEY)?.value = Utils.DEFAULT_ZONE_OPACITY
            findPreference<SwitchPreferenceCompat>("linkTopBottomBars")?.isChecked = true
            findPreference<SwitchPreferenceCompat>("override")?.isChecked = false
            findPreference<SwitchPreferenceCompat>("autoTikTok")?.isChecked = false
            findPreference<SwitchPreferenceCompat>("zones_enabled")?.isChecked = false
            findPreference<SwitchPreferenceCompat>("tap_behind")?.isChecked = false

            updateWatchedAppsSummary()
            Toast.makeText(requireContext(), "All settings have been reset to default", Toast.LENGTH_SHORT).show()
            true
        }

        findPreference<Preference>("editZones")?.setOnPreferenceClickListener {
            startZoneEditing()
            true
        }

        findPreference<Preference>("watchedApps")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_appPickerFragment)
            true
        }

        findPreference<SwitchPreferenceCompat>(AppWatcherService.PREF_KEY)?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                if (!AppWatcherService.isAccessibilityServiceEnabled(requireContext())) {
                    openAccessibilitySettings()
                }
            } else {
                // Anything the watcher started is now the user's to turn off.
                FloatingWindowService.setStartedByWatcher(requireContext(), false)
            }
            true
        }

        findPreference<SwitchPreferenceCompat>("tapBehind")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true){
                currentDialog = MessageDialogFragment.newInstance(
                    getString(R.string.tap_behind_dialog_message),
                    getString(R.string.close)
                )
                currentDialog?.show(parentFragmentManager, MessageDialogFragment.TAG)
            }

            true
        }

        findPreference<Preference>("help")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_guideIndexFragment)
            true
        }

        findPreference<Preference>("feedback")?.setOnPreferenceClickListener {
            showFeedbackDialog()
            true
        }
    }

    private fun showFeedbackDialog() {
        val options = arrayOf(
            "📧 Email Author (Catly: ccatly@gmail.com)",
            "🌐 GitHub: Catly1 (Original Author)",
            "🌐 GitHub: uzbekunknown (Developer)"
        )
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.feedback)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> composeEmail(arrayOf("ccatly@gmail.com"), "LetterBoxer App Feedback")
                    1 -> openUrl("https://github.com/catly1")
                    2 -> openUrl("https://github.com/uzbekunknown")
                }
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Edit mode lives in overlay windows on purpose: zones have to be placed on top of the app they
     * will cover, so this screen gets out of the way instead of staying in front.
     */
    private fun startZoneEditing() {
        val context = requireContext()
        if (!Settings.canDrawOverlays(context)) {
            Toast.makeText(context, R.string.zone_permission_needed, Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(context, FloatingWindowService::class.java).apply {
            action = FloatingWindowService.ACTION_EDIT_ZONES
            // Only tidy up afterwards if editing is what brought the service up.
            putExtra(FloatingWindowService.EXTRA_STOP_AFTER_EDIT, !FloatingWindowService.isRunning)
        }
        context.startForegroundService(intent)
        Toast.makeText(context, R.string.zone_edit_started, Toast.LENGTH_LONG).show()
        activity?.finish()
    }

    private fun openAccessibilitySettings() {
        // The system's accessibility confirmation dialog ignores taps while an overlay is on
        // screen, so the bars have to come down first or the Allow button does nothing.
        if (FloatingWindowService.isRunning) {
            FloatingWindowService.stopService(requireContext())
        }
        Toast.makeText(context, R.string.accessibility_permission_needed, Toast.LENGTH_LONG).show()
        awaitingAccessibility = true
        try {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        } catch (e: ActivityNotFoundException) {
            awaitingAccessibility = false
            Toast.makeText(context, R.string.accessibility_settings_unavailable, Toast.LENGTH_SHORT).show()
        }
    }

    fun composeEmail(addresses: Array<String?>?, subject: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (activity?.let { intent.resolveActivity(it.packageManager) } != null) {
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateWatchedAppsSummary()
        // Accessibility access can be revoked from system settings at any time, and without it the
        // watcher never runs, so the switch must not keep claiming the feature is on.
        val autoSwitch = findPreference<SwitchPreferenceCompat>(AppWatcherService.PREF_KEY) ?: return
        val enabled = AppWatcherService.isAccessibilityServiceEnabled(requireContext())
        if (autoSwitch.isChecked && !enabled) {
            autoSwitch.isChecked = false
            if (awaitingAccessibility) {
                showRestrictedSettingsHintIfNeeded()
            }
        }
        awaitingAccessibility = false
    }

    /**
     * A sideloaded build cannot be granted accessibility access until the user clears the
     * "restricted setting" block, and the toggle just looks greyed out with no explanation.
     */
    private fun showRestrictedSettingsHintIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        currentDialog = MessageDialogFragment.newInstance(
            getString(R.string.accessibility_restricted_hint),
            getString(R.string.close)
        )
        currentDialog?.show(parentFragmentManager, MessageDialogFragment.TAG)
    }

    private fun updateWatchedAppsSummary() {
        val preference = findPreference<Preference>("watchedApps") ?: return
        val count = AppWatcherService.watchedPackages(
            PreferenceManager.getDefaultSharedPreferences(requireContext()),
            requireContext()
        ).size
        preference.summary = if (count == 0) {
            getString(R.string.watched_apps_empty)
        } else {
            resources.getQuantityString(R.plurals.watched_apps_count, count, count)
        }
    }

    override fun onPause() {
        super.onPause()
        currentDialog?.dismiss()
    }
}
