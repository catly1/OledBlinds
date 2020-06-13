package com.example.oledsaver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.oledsaver.app.AppListItem

class AppAdapter(context: Context?, customizedListView: List<AppListItem>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var listStorage: List<AppListItem> = customizedListView
    private val context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var listViewHolder : ViewHolder
        val view: View?

        if(convertView == null){
            view = layoutInflater.inflate(R.layout.app_list_item, parent, false)
            listViewHolder = ViewHolder(view)
            view.tag = listViewHolder
//            view.setOnClickListener {
//                Toast.makeText(context,"clicking works",Toast.LENGTH_LONG).show()
//            }
        } else {
            view = convertView
            listViewHolder = view.tag as ViewHolder
        }

        listViewHolder.label.text = listStorage[position].name
        listViewHolder.icon.setImageDrawable(listStorage[position].icon)
        println(listViewHolder.label.text)
        println(listStorage.size)

        return view
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getCount(): Int {
        return listStorage.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(view: View?) {
        public val label: TextView = view?.findViewById(R.id.list_app_name) as TextView
        public val icon: ImageView = view?.findViewById(R.id.app_icon) as ImageView
    }
}