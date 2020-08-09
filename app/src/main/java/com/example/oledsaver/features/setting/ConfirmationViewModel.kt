package com.example.oledsaver.features.setting

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.example.oledsaver.app.AppListItem
import com.example.oledsaver.db.AppDatabase
import com.example.oledsaver.db.SettingRepository
import com.example.oledsaver.entity.Setting

class ConfirmationViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var repository: SettingRepository

    fun saveIcon(drawable: Drawable){
        val bitmap = drawable.toBitmap()
    }

    fun addApp(appListItem: AppListItem){

//        appListItem.icon.toBitmap()
        val setting = Setting(
            name = appListItem.name
//            icon = appListItem.icon.toBitmap()
        )
//        db.settingDao().insertAll(setting)
        repository.insert(setting)
    }
}