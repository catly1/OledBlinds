package com.catly.oledsaver.features.view.guide

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.catly.oledsaver.features.data.GuideItemRepository
import com.catly.oledsaver.features.data.model.GuideIndexItem
import java.lang.Exception

class GuideViewModel(guideItemRepository: GuideItemRepository): ViewModel() {
    val guideList = MutableLiveData<List<GuideIndexItem>>()
    private val TAG = "GuideViewModel"
    var guideMode = false
    init {
        try {
            guideList.value = guideItemRepository.getGuideIndexItems()
        } catch (e: Exception){
            Log.i(TAG,e.toString())
        }
    }
}