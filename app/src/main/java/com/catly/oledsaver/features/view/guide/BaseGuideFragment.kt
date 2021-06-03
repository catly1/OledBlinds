package com.catly.oledsaver.features.view.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.catly.oledsaver.R
import com.catly.oledsaver.features.view.ViewModelFactory

open class BaseGuideFragment(private val viewLayout: Int, private val nextDestination: Int) :
    Fragment() {

    var guideMode = false

    lateinit var nextButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(viewLayout, container, false)
        guideMode = arguments?.getBoolean("guideMode")!!
        nextButton = view.findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener {
            if (guideMode) {
                println("gets here")
                activity?.onBackPressed()
            } else {
                setRanOnce()
                findNavController().navigate(nextDestination)
            }
        }

        return view
    }

    fun setRanOnce() {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean("alreadyRanOnce", true).apply()
    }
}