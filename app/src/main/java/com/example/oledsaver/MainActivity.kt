package com.example.oledsaver

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.navigation.findNavController
//import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.oledsaver.app.AppListItem


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_first.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        val userInstalledApps = findViewById<ListView>(R.id.listView)
//        val installedApps = getInstalledApps()

//        fab.setOnClickListener {view->
//
//            showNewSettingUi()
//        }

    }

    private fun showNewSettingUi(){
        val installedApps = getInstalledApps()
//        val newFragment = NewSettingDialogFragment.newInstance()
//        newFragment.installedApps = installedApps
//        newFragment.show(supportFragmentManager, "dialog")

//        val userInstalledApps = findViewById<ListView>(R.id.listView)
//        println(userInstalledApps)
//        userInstalledApps.adapter = AppAdapter(this, installedApps)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getInstalledApps(): List<AppListItem>{
        var list : List<PackageInfo> = packageManager.getInstalledPackages(0)
        var res = ArrayList<AppListItem>()
        for (app in list){
            if(isSystemPackage(app)) {
                res.add(AppListItem(app.applicationInfo.loadLabel(packageManager).toString(), app.applicationInfo.loadIcon(packageManager)))
            }
        }
        return res
    }

    private fun isSystemPackage(app:PackageInfo): Boolean{
        return (app.applicationInfo.flags != 0 && ApplicationInfo.FLAG_SYSTEM != 0)
    }
}
