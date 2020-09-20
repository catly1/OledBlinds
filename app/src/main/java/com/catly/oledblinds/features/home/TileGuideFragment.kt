package com.catly.oledblinds.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.catly.oledblinds.R
import kotlinx.android.synthetic.main.tile_guide_fragment.*

class TileGuideFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tile_guide_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tile_guide_next_button.setOnClickListener {
            findNavController().navigate(R.id.action_tileGuideFragment_to_homeViewFragment)
        }
    }
}