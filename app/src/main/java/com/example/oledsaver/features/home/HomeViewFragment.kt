package com.example.oledsaver.features.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.oledsaver.R
import com.example.oledsaver.adapter.SavedAppAdapter
import com.example.oledsaver.entity.Setting
import com.example.oledsaver.features.setting.SharedViewModel
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeViewFragment : Fragment() {

    private val model : HomeViewModel by activityViewModels()
    private val sharedModel : SharedViewModel by activityViewModels()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_first, container, false)
        val savedSettings = rootView.findViewById<ListView>(R.id.homeListView)
//        model.repository = sharedModel.repository
//        model.getAllSavedSettings()
//        val adapter = SavedAppAdapter(activity, model.settings)
//        savedSettings.adapter = adapter
        return rootView
//         Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        var text = "Saved Apps: ${sharedModel.repository.getSettings()}"
//        settings = model.getAllSavedSettings()
//        Toast.makeText(context, text , Toast.LENGTH_SHORT).show()
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        test.setOnClickListener {
            Toast.makeText(context,"test",Toast.LENGTH_SHORT).show()
        }
    }
}
