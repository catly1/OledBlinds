package com.catly.oledsaver.features.data

import com.catly.oledsaver.features.data.model.GuideIndexItem

class GuideItemRepository(val dataSource: Datasource) {

    fun getGuideIndexItems(): List<GuideIndexItem> {
        return dataSource.loadGuideIndexItems()
    }

}