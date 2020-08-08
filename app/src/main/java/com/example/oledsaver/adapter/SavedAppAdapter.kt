package com.example.oledsaver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.example.oledsaver.R
import com.example.oledsaver.entity.Setting

class SavedAppAdapter(context: Context?, private val allSavedSettings: LiveData<List<Setting>>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val settings : List<Setting>? = allSavedSettings.value

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var listViewHolder : ViewHolder
        val view: View?

        if(convertView == null){
            view = layoutInflater.inflate(R.layout.app_list_item, parent, false)
            listViewHolder = ViewHolder(view)
            view.tag = listViewHolder
        } else {
            view = convertView
            listViewHolder = view.tag as ViewHolder
        }

        listViewHolder.label.text = settings?.get(position)?.name
        return view
    }

    override fun getItem(position: Int): Setting? {
        return settings?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        val size = settings?.size
        return size ?: 0
    }

    class ViewHolder(view: View?){
        public val label: TextView = view?.findViewById(R.id.list_app_name) as TextView
        public val icon: ImageView = view?.findViewById(R.id.app_icon) as ImageView
    }
}