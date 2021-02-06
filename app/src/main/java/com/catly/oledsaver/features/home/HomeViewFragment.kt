package com.catly.oledsaver.features.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.FloatingMenuService
import com.catly.oledsaver.features.main.MainActivity
import kotlinx.android.synthetic.main.home_fragment.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeViewFragment : Fragment() {

    private lateinit var floatingMenuServiceIntent : Intent
    private var isActive = false


    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener(){ sharedPreferences: SharedPreferences, key : String->
        when (key) {
            "isActive"->{
//                onButton?.let {
//                    it.isChecked = sharedPreferences.getBoolean(key, false)
//                }
               isActive = sharedPreferences.getBoolean(key, false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).supportActionBar!!.show()
        floatingMenuServiceIntent = Intent(activity, FloatingMenuService::class.java)
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
        initializeView(view)
    }

    private fun initializeView(view: View) {
//        onButton.setOnClickListener {
//            if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("isActive", false)){
//
//            } else {
//
//            }
//        }
//        onButton.isChecked = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("isActive", false)
//
//        onButton.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                activity?.startService(floatingMenuServiceIntent)
//                activity?.finish()
//            } else {
//                activity?.stopService(floatingMenuServiceIntent)
//            }
//        }
//
//        resetButton.setOnClickListener {
//            PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("width", 200).apply()
//            PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("height", 200).apply()
//            Toast.makeText(context, "Height and width have been reset", Toast.LENGTH_SHORT).show()
//            activity?.stopService(floatingMenuServiceIntent)
//            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("isActive", false).apply()
//            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("isLocked", false).apply()
//        }
//
//        override.isChecked = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("override",false)
//
//        override.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("override", true).apply()
//                // The toggle is enabled
//            } else {
//                PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("override", false).apply()
//                // The toggle is disabled
//            }
//        }
    }

}
