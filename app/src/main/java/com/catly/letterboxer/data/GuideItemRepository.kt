package com.catly.letterboxer.data

import com.catly.letterboxer.data.model.GuideIndexItem

class GuideItemRepository(val dataSource: Datasource) {

    fun getGuideIndexItems(): List<GuideIndexItem> {
        return dataSource.loadGuideIndexItems()
    }

}