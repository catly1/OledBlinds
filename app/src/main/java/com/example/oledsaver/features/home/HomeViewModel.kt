package com.example.oledsaver.features.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import androidx.room.Room
import com.example.oledsaver.db.AppDatabase
import com.example.oledsaver.entity.Setting

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application,
        AppDatabase::class.java,"database-name").build()

    lateinit var settings : List<Setting>

    fun getAllSavedSettings(): List<Setting> {
        settings = db.settingDao().getAll()
        return settings
    }
}