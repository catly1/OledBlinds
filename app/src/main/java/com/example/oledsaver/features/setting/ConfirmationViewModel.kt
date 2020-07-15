package com.example.oledsaver.features.setting

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.example.oledsaver.app.AppListItem
import com.example.oledsaver.db.AppDatabase
import com.example.oledsaver.entity.Setting

class ConfirmationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application,
        AppDatabase::class.java,"database-name").build()

    fun saveIcon(drawable: Drawable){
        val bitmap = drawable.toBitmap()
    }

    fun addApp(appListItem: AppListItem){

        appListItem.icon.toBitmap()
        val setting = Setting(
            name = appListItem.name,
//            icon = appListItem.icon,
            uid = 0
        )
        db.settingDao().insertAll(setting)
    }
}