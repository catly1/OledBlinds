package com.example.oledsaver.features.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.oledsaver.R
import com.example.oledsaver.adapter.SavedAppAdapter
import com.example.oledsaver.app.AppListItem
import com.example.oledsaver.entity.Setting
import com.example.oledsaver.features.floating_menu.FloatingMenuService
import com.example.oledsaver.features.setting.SharedViewModel
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeViewFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        initializeView()

    }

    private fun initializeView() {
        onButton.setOnClickListener {
            activity?.startService(Intent(activity, FloatingMenuService::class.java))
            activity?.finish()
        }
    }

}
