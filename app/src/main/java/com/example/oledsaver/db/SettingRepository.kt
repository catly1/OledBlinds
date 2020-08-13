package com.example.oledsaver.db

import androidx.lifecycle.LiveData
import com.example.oledsaver.entity.Setting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val settingDao: SettingDao
    ) {

    fun getSettings(): List<Setting> {
        return settingDao.getAll()
    }

    fun insert(setting: Setting){
        GlobalScope.launch {
            settingDao.insertAll(setting)
        }
    }



}