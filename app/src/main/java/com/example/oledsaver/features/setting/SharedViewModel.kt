package com.example.oledsaver.features.setting

import android.app.Application
import android.content.pm.PackageInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.oledsaver.app.AppListItem

class SharedViewModel : ViewModel() {
    private val selected = MutableLiveData<AppListItem>()

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