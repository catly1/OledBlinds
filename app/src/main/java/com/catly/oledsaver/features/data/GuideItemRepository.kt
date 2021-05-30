package com.catly.oledsaver.features.data

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.catly.oledsaver.features.data.model.GuideIndexItem

class GuideItemRepository(application: Application, val dataSource: Datasource) {

    lateinit var sharedPreferences: SharedPreferences

    init {
        var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
    }

    fun getGuideIndexItems(): List<GuideIndexItem> {
        return dataSource.loadGuideIndexItems()
    }

}