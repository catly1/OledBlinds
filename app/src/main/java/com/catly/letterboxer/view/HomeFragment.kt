package com.catly.oledblinds.view

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
import com.catly.oledblinds.BuildConfig
import com.catly.oledblinds.R
import com.catly.oledblinds.floating_window.FloatingWindowService
import com.google.android.material.button.MaterialButton

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

        view.findViewById<ImageButton>(R.id.news_button).setOnClickListener {
            showChangeLogDialog()
        }

        view.findViewById<TextView>(R.id.version_number).text = getString(R.string.version, BuildConfig.VERSION_NAME)

        if (checkOldVersion()){
            showChangeLogDialog()
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
