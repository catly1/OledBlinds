package com.catly.oledsaver.features.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService
import com.catly.oledsaver.features.main.MainActivity

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeViewFragment : Fragment() {

    private lateinit var floatingMenuServiceIntent : Intent
    private var isActive = false


    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener(){ sharedPreferences: SharedPreferences, key : String->
        when (key) {
            "isActive"->{
               isActive = sharedPreferences.getBoolean(key, false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).supportActionBar!!.show()
        floatingMenuServiceIntent = Intent(activity, FloatingWindowService::class.java)
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("alreadyRanOnce", true).apply()
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(activity).registerOnSharedPreferenceChangeListener(preferenceListener)
    }

}
