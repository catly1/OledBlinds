package com.catly.letterboxer.view.guide

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.catly.letterboxer.R


open class BaseGuideFragment(private val viewLayout: Int, private val nextDestination: Int) :
    Fragment() {

    private var guideMode: Boolean = false

    lateinit var nextButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(viewLayout, container, false)
        arguments?.let {
            guideMode = it.getBoolean("guideMode")
        }
        nextButton = view.findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            if (guideMode) {
                activity?.onBackPressed()
            } else {
                setRanOnce()
                findNavController().navigate(nextDestination)
            }
        }

        return view
    }

    fun setRanOnce() {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
            .putBoolean("alreadyRanOnce", true).apply()
    }

    fun launchBrowser(url: String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}