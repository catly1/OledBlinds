package com.example.oledsaver.features.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.oledsaver.R
import com.example.oledsaver.adapter.SavedAppAdapter
import com.example.oledsaver.entity.Setting
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeViewFragment : Fragment() {

    private val model : HomeViewModel by activityViewModels()
    lateinit var settings : List<Setting>


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_first, container, false)
        val savedSettings = rootView.findViewById<ListView>(R.id.homeListView)
//        val adapter = SavedAppAdapter(activity, model.settings)
//        savedSettings.adapter = adapter
        print(model)
        return rootView
//         Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        settings = model.getAllSavedSettings()

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

    }
}
