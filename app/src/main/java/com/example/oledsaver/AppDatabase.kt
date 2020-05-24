package com.example.oledsaver

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.oledsaver.setting.Setting
import com.example.oledsaver.setting.SettingDao

@Database(entities = [Setting::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): SettingDao

    companion object{
        private var INSTANCE: AppDatabase? = null

        fun getInMemoryDatabase(context: Context): AppDatabase {
            INSTANCE = INSTANCE ?: Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java).build()
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
