package com.example.oledsaver.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.oledsaver.entity.ViewParam

@Dao
interface ViewParamDao {
    @Query("SELECT * FROM ViewParam")
    fun getAll(): List<ViewParam>

    @Insert
    fun insertAll(vararg settings: ViewParam)

    @Delete
    fun delete(setting: ViewParam)
}