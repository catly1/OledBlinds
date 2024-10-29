package com.catly.oledblinds.data

import com.catly.oledblinds.data.model.GuideIndexItem

class GuideItemRepository(val dataSource: Datasource) {

    fun getGuideIndexItems(): List<GuideIndexItem> {
        return dataSource.loadGuideIndexItems()
    }

}