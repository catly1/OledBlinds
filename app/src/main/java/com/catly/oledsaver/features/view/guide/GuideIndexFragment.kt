package com.catly.oledsaver.features.view.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.catly.oledsaver.R
import com.catly.oledsaver.features.adapter.GuideIndexItemAdapter
import com.catly.oledsaver.features.view.ViewModelFactory


class GuideIndexFragment: Fragment() {
    private lateinit var guideViewModel: GuideViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        guideViewModel = ViewModelProvider(this, ViewModelFactory()).get(
            GuideViewModel::class.java)
        val view = inflater.inflate(R.layout.guide_index_fragment, container, false)
        val adapter = GuideIndexItemAdapter(requireContext(), findNavController())
        view.findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter
        guideViewModel.guideList.observe(viewLifecycleOwner) {
            adapter.update(it)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_guideIndexFragment_to_homeFragment)
        }
    }
}