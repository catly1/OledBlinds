package com.catly.oledsaver.features.data

import com.catly.oledsaver.R
import com.catly.oledsaver.features.data.model.GuideIndexItem

class Datasource {
    fun loadGuideIndexItems(): List<GuideIndexItem>{
        return listOf(
            GuideIndexItem(R.string.guide1, R.id.action_guideIndexFragment_to_permissionFragment),
            GuideIndexItem(R.string.guide2, R.id.action_guideIndexFragment_to_guideFragment),
            GuideIndexItem(R.string.guide3, R.id.action_guideIndexFragment_to_tileGuideFragment),
            GuideIndexItem(R.string.guide4, R.id.action_guideIndexFragment_to_intentGuideFragment)
        )
    }
}