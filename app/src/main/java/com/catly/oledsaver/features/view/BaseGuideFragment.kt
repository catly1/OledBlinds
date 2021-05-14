package com.catly.oledsaver.features.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager

open class BaseGuideFragment(private val viewLayout: Int, private val nextDestination: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(viewLayout, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        next_button.setOnClickListener {
            setRanOnce()
            findNavController().navigate(nextDestination)
        }
    }

    override fun onPause() {
        super.onPause()
        guideModeOff()
    }

    fun setRanOnce(){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("alreadyRanOnce", true).apply()
    }

    fun getRanOnce(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            "alreadyRanOnce",
            false
        )
    }

    fun guideModeOn(){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("guideMode", true).apply()
    }

    fun guideModeOff(){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("guideMode", false).apply()
    }

    fun getGuideMode(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
            "guideMode",
            false
        )
    }
}