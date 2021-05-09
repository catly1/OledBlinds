package com.catly.oledsaver.features.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MessageDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("test")
            .setPositiveButton("press") { _, _ -> }
            .create()


    companion object {
        const val TAG = "MessageDialog"
    }
}