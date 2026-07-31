package com.catly.letterboxer.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

/**
 * Arguments rather than constructor parameters: the FragmentManager recreates dialogs after a
 * configuration change or process death using the no-argument constructor, and a class without one
 * throws Fragment.InstantiationException.
 */
class MessageDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(arguments?.getString(ARG_MESSAGE).orEmpty())
            .setPositiveButton(arguments?.getString(ARG_POSITIVE_BUTTON).orEmpty()) { _, _ -> }
            .create()


    companion object {
        const val TAG = "MessageDialog"
        private const val ARG_MESSAGE = "message"
        private const val ARG_POSITIVE_BUTTON = "positiveButton"

        fun newInstance(message: String, posButton: String) = MessageDialogFragment().apply {
            arguments = bundleOf(ARG_MESSAGE to message, ARG_POSITIVE_BUTTON to posButton)
        }
    }

}
