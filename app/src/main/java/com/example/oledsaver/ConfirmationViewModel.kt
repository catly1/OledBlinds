package com.example.oledsaver

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room

class ConfirmationViewModel(application: Application) : AndroidViewModel(application) {
    val db = Room.databaseBuilder(application,AppDatabase::class.java,"database-name")
}