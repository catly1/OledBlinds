package com.example.oledsaver.db

import com.example.oledsaver.entity.ViewParam
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewParamRepository @Inject constructor(
    private val viewParamDao: ViewParamDao
) {

    fun getAllViewParams(): List<ViewParam>{
        return viewParamDao.getAll()
    }

    fun insert(viewParam: ViewParam){
        GlobalScope.launch {
            viewParamDao.insertAll(viewParam)
        }
    }

    fun delete(viewParam: ViewParam){
        GlobalScope.launch {
            viewParamDao.delete(viewParam)
        }
    }
}