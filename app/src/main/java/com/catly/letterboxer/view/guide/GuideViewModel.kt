package com.catly.letterboxer.view.guide

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.catly.letterboxer.data.GuideItemRepository
import com.catly.letterboxer.data.model.GuideIndexItem
import java.lang.Exception

class GuideViewModel(guideItemRepository: GuideItemRepository): ViewModel() {
    val guideList = MutableLiveData<List<GuideIndexItem>>()
    private val TAG = "GuideViewModel"
    init {
        try {
            guideList.value = guideItemRepository.getGuideIndexItems()
        } catch (e: Exception){
            Log.i(TAG,e.toString())
        }
    }
}