package com.example.oledsaver.entity

import android.view.WindowManager
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ViewParam (
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String?
//    @ColumnInfo(name = "params") val params: ArrayList<WindowManager.LayoutParams>
)