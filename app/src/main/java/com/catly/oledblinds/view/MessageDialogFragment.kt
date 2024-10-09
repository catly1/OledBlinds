package com.catly.oledblinds.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MessageDialogFragment(private val message: String, private val posButton: String): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(posButton) { _, _ -> }
            .create()


    companion object {
        const val TAG = "MessageDialog"
    }

}