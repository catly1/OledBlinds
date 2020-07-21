package com.example.oledsaver.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.oledsaver.entity.Setting

class SavedAppAdapter: BaseAdapter() {

    lateinit var savedAppList: List<Setting>

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        savedAppList =
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }
}