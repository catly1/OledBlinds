package com.catly.oledsaver.features.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.catly.oledsaver.R
import kotlinx.android.synthetic.main.guide_fragment.*

class GuideFragment : BaseGuideFragment(R.layout.guide_fragment, R.id.action_guideFragment_to_tileGuideFragment) {}