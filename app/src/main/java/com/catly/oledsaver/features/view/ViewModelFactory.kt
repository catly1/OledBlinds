package com.catly.oledsaver.features.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.catly.oledsaver.features.data.Datasource
import com.catly.oledsaver.features.data.GuideItemRepository
import com.catly.oledsaver.features.view.guide.GuideViewModel

class ViewModelFactory : ViewModelProvider.Factory  {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuideViewModel::class.java)) {
            return GuideViewModel(
                    guideItemRepository = GuideItemRepository(dataSource = Datasource())
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}