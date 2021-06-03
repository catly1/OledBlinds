package com.catly.oledsaver.features.view.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.catly.oledsaver.R
import com.catly.oledsaver.databinding.GuideIndexFragmentBinding
import com.catly.oledsaver.features.adapter.GuideIndexItemAdapter
import com.catly.oledsaver.features.view.ViewModelFactory


class GuideIndexFragment: Fragment() {
    private lateinit var guideViewModel: GuideViewModel
    private lateinit var binding: GuideIndexFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        guideViewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application)).get(
            GuideViewModel::class.java)
        binding = GuideIndexFragmentBinding.inflate(layoutInflater)
//        val view = inflater.inflate(R.layout.guide_index_fragment, container, false)
        val adapter = GuideIndexItemAdapter(requireContext(), findNavController())
        binding.recyclerView.adapter = adapter
        guideViewModel.guideList.observe(viewLifecycleOwner) {
            adapter.update(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24dp)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_guideIndexFragment_to_homeFragment)
        }
    }
}