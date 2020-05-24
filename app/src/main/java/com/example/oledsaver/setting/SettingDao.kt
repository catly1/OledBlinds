package com.example.oledsaver.setting

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SettingDao {
    @Query("SELECT * FROM setting")
    fun getAll(): List<Setting>

    @Insert
    fun insertAll(vararg users: Setting)

    @Delete
    fun delete(user: Setting)
}