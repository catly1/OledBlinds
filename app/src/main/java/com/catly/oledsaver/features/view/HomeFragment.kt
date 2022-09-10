package com.catly.oledsaver.features.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService
import com.google.android.material.button.MaterialButton

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "alreadyRanOnce",
                false
            ) || !Settings.canDrawOverlays(activity)
        ) {
            findNavController().navigate(R.id.action_homeFragment_to_permissionFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, SettingsFragment())
            .commit()
        return inflater.inflate(R.layout.home_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.startButton).setOnClickListener {
            if (!FloatingWindowService.isRunning) {
                context?.let { it1 -> FloatingWindowService.startService(it1)}
//                activity?.finish()
            } else {
                context?.let { it1 -> FloatingWindowService.stopService(it1) }
            }
        }
    }
}
