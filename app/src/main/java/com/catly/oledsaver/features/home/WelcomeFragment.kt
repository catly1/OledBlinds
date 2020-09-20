package com.catly.oledsaver.features.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.main.MainActivity
import kotlinx.android.synthetic.main.welcome_fragment.*

class WelcomeFragment : Fragment() {
    private val drawOtherAppPermissionCode = 2084
    private var ranOnceAlready: Boolean = false

    override fun onResume() {
        super.onResume()
        if (ranOnceAlready && Settings.canDrawOverlays(activity) && findNavController().currentDestination?.id == R.id.welcomeFragment){
            findNavController().navigate(R.id.action_welcomeFragment_to_homeViewFragment)
        }

        if (Settings.canDrawOverlays(activity) && findNavController().currentDestination?.id == R.id.welcomeFragment) {
            findNavController().navigate(R.id.action_welcomeFragment_to_guideFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).supportActionBar!!.hide()
        ranOnceAlready = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("alreadyRanOnce", false)

        if(ranOnceAlready && Settings.canDrawOverlays(activity) && findNavController().currentDestination?.id == R.id.welcomeFragment){
            findNavController().navigate(R.id.action_welcomeFragment_to_homeViewFragment)
        }

        if (Settings.canDrawOverlays(activity) && findNavController().currentDestination?.id == R.id.welcomeFragment) {
            findNavController().navigate(R.id.action_welcomeFragment_to_guideFragment)
        }


        return inflater.inflate(R.layout.welcome_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        set_permission_button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(activity))
            {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity?.packageName}"))
                startActivityForResult(intent, drawOtherAppPermissionCode)
            } else {
                findNavController().navigate(R.id.action_welcomeFragment_to_guideFragment)
            }
        }
    }
}