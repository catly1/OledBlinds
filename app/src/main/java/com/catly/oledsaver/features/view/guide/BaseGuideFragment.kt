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

open class BaseGuideFragment(private val viewLayout: Int, private val nextDestination: Int) : Fragment() {
    private lateinit var guideViewModel: GuideViewModel
    var guideMode = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(viewLayout, container, false)
        guideViewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application)).get(
            GuideViewModel::class.java)
        guideMode = arguments?.getBoolean("guideMode")!!
        view.findViewById<Button>(R.id.next_button).setOnClickListener {
//            println("guide mode? " + guideViewModel.guideMode)
            println("guide mode is: $guideMode")
            if (guideMode){
                println("gets here")
//                guideViewModel.guideMode = false
                activity?.onBackPressed()
            } else {
                setRanOnce()
                findNavController().navigate(nextDestination)
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        guideViewModel
    }
    fun setRanOnce(){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("alreadyRanOnce", true).apply()
    }
}