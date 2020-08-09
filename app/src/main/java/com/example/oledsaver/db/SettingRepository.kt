package com.example.oledsaver.db

import androidx.lifecycle.LiveData
import com.example.oledsaver.entity.Setting
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val settingDao: SettingDao
    ) {

    fun getSettings(): LiveData<List<Setting>> {
        return settingDao.getAll()
    }

    fun insert(setting: Setting){
        settingDao.insertAll(setting)
    }


}