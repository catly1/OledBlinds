package com.example.oledsaver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.oledsaver.app.AppListItem

class AppAdapter(var layoutInflater: LayoutInflater, var listStorage: List<AppListItem>, context: Context, customizedListView: List<AppListItem>) : BaseAdapter() {
    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        listStorage = customizedListView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getCount(): Int {
        return listStorage.size
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

}