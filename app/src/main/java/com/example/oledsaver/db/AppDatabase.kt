package com.example.oledsaver.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.oledsaver.entity.Setting

@Database(entities = [Setting::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingDao(): SettingDao

    companion object{
        private var INSTANCE: AppDatabase? = null

        fun getInMemoryDatabase(context: Context): AppDatabase {
            INSTANCE = INSTANCE
                ?: Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java).build()
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
