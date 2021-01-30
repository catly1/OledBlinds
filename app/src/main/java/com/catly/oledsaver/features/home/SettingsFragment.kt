package com.catly.oledsaver.features.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<Preference>("turnServiceOn")?.setOnPreferenceClickListener {
            activity?.startService(Intent(activity, FloatingMenuService::class.java))
            activity?.finish()
            true
        }
        findPreference<Preference>("reset")?.setOnPreferenceClickListener {
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("width", 200).apply()
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("height", 200).apply()
            Toast.makeText(context, "Height and width have been reset", Toast.LENGTH_SHORT).show()
            activity?.stopService(Intent(activity, FloatingMenuService::class.java))
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("isActive", false).apply()
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("isLocked", false).apply()
        true
        }
    }
}