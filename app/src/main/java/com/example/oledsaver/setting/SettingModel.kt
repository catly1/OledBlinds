package com.example.oledsaver.setting

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "name") val name: String?
)