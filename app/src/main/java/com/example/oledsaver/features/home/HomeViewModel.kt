package com.example.oledsaver.features.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import androidx.room.Room
import com.example.oledsaver.db.AppDatabase

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application,
        AppDatabase::class.java,"database-name").build()
}