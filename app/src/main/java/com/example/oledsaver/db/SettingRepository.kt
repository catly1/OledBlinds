package com.example.oledsaver.db

import androidx.lifecycle.LiveData
import com.example.oledsaver.entity.Setting
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val settingDao: SettingDao
    ) {

    fun getSettings(): List<Setting> {
        println("before crash")
        var test = settingDao.testAll()
        println("test works")
        println(test.value)
        var list: List<Setting> = settingDao.getAll()
        println("after crash?")
        println(list)

        return list
    }


}