package com.example.oledsaver

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment


class NewSettingDialogFragment: DialogFragment() {
//    interface NewSettingDialogListener {
//        fun onDialogPositiveClick(dialog: DialogFragment, task: String)
//        fun onDialogNegativeClick(dialog: DialogFragment)
//    }
//
//    var newSettingDialogListener: NewSettingDialogListener? = null

    companion object {
        fun newInstance(): NewSettingDialogFragment{
            //            val args = Bundle()
//            args.putInt("dialog_title", title)
//            newSettingDialogFragment.arguments = args
            return NewSettingDialogFragment()
        }
    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       return inflater!!.inflate(R.layout.dialog_new_setting, container, false)
    }



}