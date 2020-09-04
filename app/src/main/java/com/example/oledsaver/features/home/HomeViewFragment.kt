package com.example.oledsaver.features.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.example.oledsaver.R
import com.example.oledsaver.features.floating_menu.FloatingMenuService
import kotlinx.android.synthetic.main.home_fragment.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()

    }

    private fun initializeView() {
        onButton.setOnClickListener {
            activity?.startService(Intent(activity, FloatingMenuService::class.java))
            activity?.finish()
            PreferenceManager.getDefaultSharedPreferences(activity).edit()
                .putBoolean("isActive", true).apply()
        }
    }

}
