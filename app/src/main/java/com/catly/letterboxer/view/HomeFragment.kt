package com.catly.letterboxer.view

import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.catly.letterboxer.BuildConfig
import com.catly.letterboxer.R

import com.catly.letterboxer.floating_window.FloatingWindowService
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {
    private var currentDialog : DialogFragment? = null
    override fun onResume() {
        super.onResume()
        if (!PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(
                "alreadyRanOnce",
                false
            ) || !Settings.canDrawOverlays(activity)
        ) {
            findNavController().navigate(R.id.action_homeFragment_to_permissionFragment)
        }
        updateServiceStateUI(view)
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
        val startBtn = view.findViewById<MaterialButton>(R.id.startButton)
        startBtn.setOnClickListener {
            if (!FloatingWindowService.isRunning) {
                context?.let { it1 -> FloatingWindowService.startService(it1)}
            } else {
                context?.let { it1 -> FloatingWindowService.stopService(it1) }
            }
            startBtn.postDelayed({ updateServiceStateUI(view) }, 300)
        }

        view.findViewById<ImageButton>(R.id.news_button).setOnClickListener {
            showChangeLogDialog()
        }

        view.findViewById<TextView>(R.id.version_number).text = getString(R.string.version, BuildConfig.VERSION_NAME)

        updateServiceStateUI(view)

        if (checkOldVersion()){
            showChangeLogDialog()
        }
    }

    private fun updateServiceStateUI(view: View?) {
        if (view == null) return
        val startBtn = view.findViewById<MaterialButton>(R.id.startButton) ?: return
        val subtitle = view.findViewById<TextView>(R.id.master_status_subtitle) ?: return
        val context = view.context ?: return

        val colorPrimary = MaterialColors.getColor(view, androidx.appcompat.R.attr.colorPrimary, ContextCompat.getColor(context, R.color.m3_accent_primary))
        val colorOnPrimary = MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnPrimary, ContextCompat.getColor(context, R.color.m3_pitch_black))
        val colorSurfaceStroke = ContextCompat.getColor(context, R.color.m3_surface_stroke)
        val colorTextPrimary = ContextCompat.getColor(context, R.color.m3_text_primary)

        if (FloatingWindowService.isRunning) {
            startBtn.text = getString(R.string.stop)
            startBtn.backgroundTintList = ColorStateList.valueOf(colorSurfaceStroke)
            startBtn.setTextColor(colorTextPrimary)
            startBtn.iconTint = ColorStateList.valueOf(colorTextPrimary)
            subtitle.text = "Overlay is currently Active"
        } else {
            startBtn.text = getString(R.string.start)
            startBtn.backgroundTintList = ColorStateList.valueOf(colorPrimary)
            startBtn.setTextColor(colorOnPrimary)
            startBtn.iconTint = ColorStateList.valueOf(colorOnPrimary)
            subtitle.text = "Tap to toggle black bars overlay"
        }
    }

    private fun showChangeLogDialog(){
        currentDialog = ChangesDialog()
        currentDialog?.show(parentFragmentManager, javaClass.simpleName)
    }

    private fun checkOldVersion(): Boolean {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val savedVersion = sharedPreference.getInt("release", 0)
        val currentVersion = BuildConfig.VERSION_CODE
        return if (currentVersion > savedVersion){
            sharedPreference.edit()
                .putInt("release", currentVersion)
                .apply()
            true
        } else false
    }
}
