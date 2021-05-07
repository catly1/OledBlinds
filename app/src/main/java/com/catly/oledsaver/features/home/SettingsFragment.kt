package com.catly.oledsaver.features.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<Preference>("turnServiceOn")?.setOnPreferenceClickListener {
            if (!FloatingWindowService.isRunning) {
                context?.let { it1 -> FloatingWindowService.startService(it1)}
                activity?.finish()
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
    }
}