package com.example.oledsaver

import android.os.Bundle
import androidx.fragment.app.DialogFragment

class NewSettingDialogFragment: DialogFragment() {
    interface NewSettingDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, task: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    var newSettingDialogListener: NewSettingDialogListener? = null

    companion object {
        fun newInstance(title: Int): NewSettingDialogFragment{
            val newSettingDialogFragment = NewSettingDialogFragment()
            val args = Bundle()
        }
    }
}