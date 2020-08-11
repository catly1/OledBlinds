package com.example.oledsaver.entity

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String?
//    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val icon: Bitmap
)