package com.example.oledsaver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.oledsaver.R
import com.example.oledsaver.entity.Setting

class SavedAppAdapter(context: Context?, private val allSavedSettings: List<Setting>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var listViewHolder : ViewHolder
        val view: View?

        if(convertView == null){
            view = layoutInflater.inflate(R.layout.app_list_item, parent, false)
        } else {
            view = convertView
        }

        return view
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