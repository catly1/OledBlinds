package com.catly.oledblinds.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.catly.oledblinds.data.Datasource
import com.catly.oledblinds.data.GuideItemRepository
import com.catly.oledblinds.view.guide.GuideViewModel

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