package com.catly.oledsaver.features.view.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R

open class BaseGuideFragment(private val viewLayout: Int, private val nextDestination: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(viewLayout, container, false)
        view.findViewById<Button>(R.id.next_button).setOnClickListener {
            if (getGuideMode()){
                guideModeOff()
                activity?.onBackPressed()
            } else {
                setRanOnce()
                findNavController().navigate(nextDestination)
            }
        }

        return view
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