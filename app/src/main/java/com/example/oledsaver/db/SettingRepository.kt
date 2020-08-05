package com.example.oledsaver.db

import com.example.oledsaver.entity.Setting
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val executor: Executor,
    private val settingDao: SettingDao
    ) {

    fun getSettings(): List<Setting> {
        return settingDao.getAll()
    }
}