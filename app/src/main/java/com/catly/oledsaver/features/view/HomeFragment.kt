package com.catly.oledsaver.features.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.FloatingWindowService
import com.catly.oledsaver.features.main.MainActivity
import kotlinx.android.synthetic.main.home_fragment.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

//    private lateinit var floatingMenuServiceIntent : Intent

    override fun onResume() {
        super.onResume()
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "alreadyRanOnce",
                false
            ) || !Settings.canDrawOverlays(activity)){
            findNavController().navigate(R.id.action_homeFragment_to_permissionFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (requireActivity() as MainActivity).supportActionBar!!.show()
//        floatingMenuServiceIntent = Intent(activity, FloatingWindowService::class.java)
//        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("alreadyRanOnce", true).apply()
//        parentFragmentManager
//            .beginTransaction()
//            .replace(R.id.settings_container, SettingsFragment())
//            .commit()

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, SettingsFragment())
            .commit()
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    fun composeEmail(addresses: Array<String?>?, subject: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (activity?.let { intent.resolveActivity(it.packageManager) } != null) {
            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.feedback -> {
                    composeEmail(arrayOf("ccatly@gmail.com"), "Floating Translate App Feedback")
                    true
                }
                else -> false
            }
        }
//        onButton.setOnClickListener {
//            startMediaProjection.launch()
//        }
    }
}
