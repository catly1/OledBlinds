package com.catly.oledsaver.features.view.guide

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.navigation.fragment.findNavController
import com.catly.oledsaver.R
import com.catly.oledsaver.features.view.guide.BaseGuideFragment
import kotlinx.android.synthetic.main.permission_fragment.*

class PermissionGuideFragment : BaseGuideFragment(R.layout.permission_fragment,R.id.action_permissionFragment_to_guideFragment) {
    private val drawOtherAppPermissionCode = 2084
    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(activity) && findNavController().currentDestination?.id == R.id.permissionFragment) {
//            findNavController().navigate(R.id.action_permissionFragment_to_guideFragment)
            set_permission_button.visibility = View.GONE
            next_button.visibility = View.VISIBLE
        } else {
            next_button.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        set_permission_button.setOnClickListener {
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