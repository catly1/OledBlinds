package com.catly.oledsaver.features.view.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.catly.oledsaver.R
import com.catly.oledsaver.features.adapter.GuideIndexItemAdapter
import com.catly.oledsaver.features.view.ViewModelFactory
import kotlinx.android.synthetic.main.home_fragment.*

class GuideIndexFragment: Fragment() {
    private lateinit var guideViewModel: GuideViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        guideViewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application)).get(
            GuideViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_guide_index, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = GuideIndexItemAdapter(requireContext(), findNavController())
        recyclerView.adapter = adapter
        guideViewModel.guideList.observe(viewLifecycleOwner) {
            adapter.update(it)
            guideViewModel.guideMode = true
            println("guide mode? " + guideViewModel.guideMode)
        }
        recyclerView.setHasFixedSize(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_guideIndexFragment_to_homeFragment)
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        guideViewModel.guideMode = false
//    }
}