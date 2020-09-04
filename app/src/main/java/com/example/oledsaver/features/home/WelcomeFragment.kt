package com.example.oledsaver.features.home

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
import com.example.oledsaver.R
import com.example.oledsaver.features.main.MainActivity
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.welcome_page.*

class WelcomeFragment : Fragment() {
    private val drawOtherAppPermissionCode = 2084

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(activity)) {
            findNavController().navigate(R.id.action_welcomeFragment_to_guideFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).supportActionBar!!.hide()

        if (Settings.canDrawOverlays(activity)) {
            findNavController().navigate(R.id.action_welcomeFragment_to_guideFragment)
        }

        return inflater.inflate(R.layout.welcome_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        set_permission_button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(activity))
            {
                //If the draw over permission is not available open the settings screen
                //to grant the permission.
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity?.packageName}"))
                startActivityForResult(intent, drawOtherAppPermissionCode)
            } else {
                findNavController().navigate(R.id.action_welcomeFragment_to_guideFragment)
//            initializeView()
            }
        }
    }
}