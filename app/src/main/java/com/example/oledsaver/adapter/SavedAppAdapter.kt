package com.example.oledsaver.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.oledsaver.entity.Setting

class SavedAppAdapter(private val allSavedSettings: List<Setting>) : BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var listViewHolder : ViewHolder
    }

    override fun getItem(position: Int): Any {
        return allSavedSettings[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return allSavedSettings.size
    }

    class ViewHolder(view: View?){

    }
}