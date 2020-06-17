package com.example.oledsaver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.oledsaver.app.AppListItem

class SharedViewModel : ViewModel() {
    private val selected = MutableLiveData<AppListItem>()

    fun select(item: AppListItem) {
        selected.value = item
    }
}