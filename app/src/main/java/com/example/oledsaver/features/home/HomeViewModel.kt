package com.example.oledsaver.features.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import androidx.room.Room
import com.example.oledsaver.db.AppDatabase
import com.example.oledsaver.db.SettingRepository
import com.example.oledsaver.entity.Setting

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var repository: SettingRepository
    lateinit var settings : Array<Setting>

    fun getAllSavedSettings(): Array<Setting> {

        settings = repository.getSettings()
        return settings
    }
}