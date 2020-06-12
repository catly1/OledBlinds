package com.example.oledsaver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.example.oledsaver.app.AppListItem


class NewSettingDialogFragment: DialogFragment() {
//    interface NewSettingDialogListener {
//        fun onDialogPositiveClick(dialog: DialogFragment, task: String)
//        fun onDialogNegativeClick(dialog: DialogFragment)
//    }
//
//    var newSettingDialogListener: NewSettingDialogListener? = null
    var installedApps: List<AppListItem> = ArrayList();
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

//        val userInstalledApps = view?.findViewById<ListView>(R.id.listView)
//        userInstalledApps?.adapter = AppAdapter(activity!!, installedApps)
//        println(installedApps)
//        println(userInstalledApps)
       return inflater!!.inflate(R.layout.dialog_new_setting, container, false)
    }



}