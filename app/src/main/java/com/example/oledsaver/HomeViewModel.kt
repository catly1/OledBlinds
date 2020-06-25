package com.example.oledsaver

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import androidx.room.Room

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application,AppDatabase::class.java,"database-name").build()
}