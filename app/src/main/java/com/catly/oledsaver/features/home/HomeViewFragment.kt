package com.catly.oledsaver.features.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).supportActionBar!!.show()
        floatingMenuServiceIntent = Intent(activity, FloatingMenuService::class.java)
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("alreadyRanOnce", true).apply()
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()

    }

    private fun initializeView() {
        onButton.setOnClickListener {
            activity?.startService(floatingMenuServiceIntent)
            activity?.finish()
            PreferenceManager.getDefaultSharedPreferences(activity).edit()
                .putBoolean("isActive", true).apply()
        }

        resetButton.setOnClickListener {
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("width", 200).apply()
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("height", 200).apply()
            Toast.makeText(context, "Height and width have been reset", Toast.LENGTH_SHORT).show()
            activity?.stopService(floatingMenuServiceIntent)
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("isActive", false).apply()
        }
    }

}