package com.example.oledsaver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.oledsaver.app.AppListItem
import kotlinx.android.synthetic.main.fragment_confirmation.*

class ConfirmationFragment: Fragment() {

    private val model: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app : AppListItem? = model.get()
        Toast.makeText(activity,"selected app is ${app?.name}", Toast.LENGTH_LONG).show()
        saveButton.setOnClickListener {

            findNavController().navigate(R.id.action_ConfirmationFragment_to_FirstFragment)
        }

    }
}