package com.example.oledsaver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.oledsaver.app.AppListItem

class AppAdapter(context: Context, customizedListView:List<AppListItem>):BaseAdapter() {
    private val layoutInflater:LayoutInflater
    private val listStorage:List<AppListItem>
    val count:Int
        get() {
            return listStorage.size
        }
    init{
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        listStorage = customizedListView
    }
    fun getItem(position:Int):Any {
        return position
    }
    fun getItemId(position:Int):Long {
        return position.toLong()
    }
    fun getView(position:Int, convertView: View, parent: ViewGroup):View {
        val listViewHolder:ViewHolder
        if (convertView == null)
        {
            listViewHolder = ViewHolder()
            convertView = layoutInflater.inflate(R.layout.installed_app_list, parent, false)
            listViewHolder.textInListView = convertView.findViewById(R.id.list_app_name) as TextView
            listViewHolder.imageInListView = convertView.findViewById(R.id.app_icon) as ImageView
            convertView.setTag(listViewHolder)
        }
        else
        {
            listViewHolder = convertView.getTag() as ViewHolder
        }
        listViewHolder.textInListView.setText(listStorage.get(position).getName())
        listViewHolder.imageInListView.setImageDrawable(listStorage.get(position).getIcon())
        return convertView
    }
    internal class ViewHolder {
        var textInListView:TextView
        var imageInListView:ImageView
    }
}