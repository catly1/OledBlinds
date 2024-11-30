package com.catly.letterboxer.data


import com.catly.letterboxer.R
import com.catly.letterboxer.data.model.GuideIndexItem


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