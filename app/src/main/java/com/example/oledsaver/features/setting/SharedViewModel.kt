package com.example.oledsaver.features.setting

import android.app.Application
import android.content.pm.PackageInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.oledsaver.app.AppListItem
import com.example.oledsaver.db.AppDatabase
import com.example.oledsaver.db.SettingRepository

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val selected = MutableLiveData<AppListItem>()
    private val dao = AppDatabase.getDatabase(application).settingDao()
    val repository: SettingRepository =
        SettingRepository(dao)
    val installedApps : List<AppListItem>? = null

    fun set(item: AppListItem) {
        println(item)
        selected.value = item
    }

    fun get(): AppListItem? {
        println(selected)
        return selected.value
    }

}