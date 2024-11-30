package com.catly.letterboxer.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.catly.letterboxer.data.Datasource
import com.catly.letterboxer.view.guide.GuideViewModel
import com.catly.letterboxer.data.GuideItemRepository


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