package com.example.oledsaver.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.oledsaver.entity.Setting

@Dao
interface SettingDao {
    @Query("SELECT * FROM setting")
    fun getAll(): List<Setting>

    @Insert
    fun insertAll(vararg settings: Setting)

    @Delete
    fun delete(setting: Setting)
}