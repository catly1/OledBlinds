package com.catly.oledsaver.features.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.catly.oledsaver.R
import com.catly.oledsaver.features.adapter.GuideIndexItemAdapter
import com.catly.oledsaver.features.data.Datasource
import kotlinx.android.synthetic.main.home_fragment.*

class GuideIndexFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_guide_index, container, false)
        val myDataSet = Datasource().loadGuideIndexItems()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = GuideIndexItemAdapter(requireContext(), myDataSet, findNavController())
        recyclerView.setHasFixedSize(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guideModeOn()
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_guideIndexFragment_to_homeFragment)
        }
    }

    fun guideModeOn(){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("guideMode", true).apply()
    }
}