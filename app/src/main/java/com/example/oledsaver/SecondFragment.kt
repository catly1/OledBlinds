package com.example.oledsaver

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.oledsaver.app.AppListItem

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_second, container, false)
        val userInstalledApps = rootView.findViewById<ListView>(R.id.listView)
        val installedApps = getInstalledApps()
        val adapter = AppAdapter(activity, installedApps)
        userInstalledApps.adapter = adapter
        userInstalledApps
        userInstalledApps.setOnItemClickListener {parent, view, position, id ->
            val row = adapter.getItem(position)
            Toast.makeText(activity,"you clicked $row", Toast.LENGTH_LONG).show()
        }
        // Inflate the layout for this fragment
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val userInstalledApps = view.findViewById<ListView>(R.id.listView)
//        val installedApps = getInstalledApps()
//        println(installedApps)
//        println(userInstalledApps)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    private fun getInstalledApps(): List<AppListItem>{
        var list : List<PackageInfo> = activity?.packageManager?.getInstalledPackages(0) as List<PackageInfo>
        var res = ArrayList<AppListItem>()
        for (app in list){
            if(isSystemPackage(app)) {
                res.add(AppListItem(app.applicationInfo.loadLabel(activity!!.packageManager).toString(), app.applicationInfo.loadIcon(
                    activity!!.packageManager)))
            }
        }
        return res
    }


    private fun isSystemPackage(app:PackageInfo): Boolean{
        return (app.applicationInfo.flags != 0 && ApplicationInfo.FLAG_SYSTEM != 0)
    }
}
