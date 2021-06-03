package com.catly.oledsaver.features.view.guide

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.catly.oledsaver.R

class PermissionGuideFragment : BaseGuideFragment(R.layout.permission_fragment,R.id.action_permissionFragment_to_guideFragment) {
    private val drawOtherAppPermissionCode = 2084
    private lateinit var setPermissionButton: Button
    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(activity) && findNavController().currentDestination?.id == R.id.permissionFragment) {
            setPermissionButton.visibility = View.GONE
            nextButton.visibility = View.VISIBLE
        } else {
            nextButton.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPermissionButton = view.findViewById<Button>(R.id.set_permission_button)
       setPermissionButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(activity))
            {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity?.packageName}")
                )
                startActivityForResult(intent, drawOtherAppPermissionCode)
            } else {
                findNavController().navigate(R.id.action_permissionFragment_to_guideFragment)
            }
        }
    }

}