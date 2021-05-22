package com.catly.oledsaver.features.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<Preference>("turnServiceOn")?.setOnPreferenceClickListener {
            if (!FloatingWindowService.isRunning) {
                context?.let { it1 -> FloatingWindowService.startService(it1)}
//                activity?.finish()
            } else {
                context?.let { it1 -> FloatingWindowService.stopService(it1) }
            }
            true
        }
        findPreference<Preference>("reset")?.setOnPreferenceClickListener {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity).edit()
            sharedPreferences.putInt("width", 200).apply()
            sharedPreferences.putInt("height", 200).apply()
            Toast.makeText(context, "Height and width have been reset", Toast.LENGTH_SHORT).show()
            activity?.stopService(Intent(activity, FloatingWindowService::class.java))
//            sharedPreferences.putBoolean("isActive", false).apply()
            sharedPreferences.putBoolean("isLocked", false).apply()
            sharedPreferences.putString("statusBarSize", "92").apply()
            findPreference<EditTextPreference>("statusBarSize")?.text = "92"
        true
        }

        findPreference<SwitchPreferenceCompat>("tapBehind")?.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue == true){
                MessageDialogFragment(getString(R.string.tap_behind_dialog_message), getString(R.string.close)).show(parentFragmentManager, MessageDialogFragment.TAG)
            }

            true
        }

        findPreference<Preference>("help")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_guideIndexFragment)
            true
        }
    }
}