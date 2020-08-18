package com.example.oledsaver.features.setting

import android.app.Application
import android.content.pm.PackageInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.oledsaver.app.AppListItem
import com.example.oledsaver.db.AppDatabase
import com.example.oledsaver.db.SettingRepository
import com.example.oledsaver.entity.Setting

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val toBeSaved = MutableLiveData<AppListItem>()
    private val currentSetting = MutableLiveData<Setting>()
    private val dao = AppDatabase.getDatabase(application).settingDao()
    val repository: SettingRepository =
        SettingRepository(dao)
    val installedApps : List<AppListItem>? = null

    fun setSettingToBeSaved(item: AppListItem) {
        toBeSaved.value = item
    }

    fun getSettingToBeSaved(): AppListItem? {
        return toBeSaved.value
    }

    fun setCurrentSetting(setting: Setting){
        currentSetting.value = setting
    }

    fun getCurrentSetting(): Setting?{
        return currentSetting.value
    }
}