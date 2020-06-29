package com.example.oledsaver.features.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.oledsaver.R
import com.example.oledsaver.app.AppListItem
import kotlinx.android.synthetic.main.fragment_confirmation.*

class ConfirmationFragment: Fragment() {

    private val sharedModel: SharedViewModel by activityViewModels()
    private val model : ConfirmationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app : AppListItem? = sharedModel.get()
        Toast.makeText(activity,"selected app is ${app?.name}", Toast.LENGTH_LONG).show()
        saveButton.setOnClickListener {
//            if (app != null) {
//                model.addApp(app)
//            }
            app?.let { it1 ->
                model.addApp(it1)
                findNavController().navigate(R.id.action_ConfirmationFragment_to_FirstFragment)
            }

        }

    }
}