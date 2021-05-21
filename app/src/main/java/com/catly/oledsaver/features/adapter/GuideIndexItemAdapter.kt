package com.catly.oledsaver.features.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.catly.oledsaver.R
import com.catly.oledsaver.features.model.GuideIndexItem

class GuideIndexItemAdapter(
    private val context: Context,
    private val dataSet: List<GuideIndexItem>,
    private val findNavController: NavController
) : RecyclerView.Adapter<GuideIndexItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.guide_index_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataSet[position]
        holder.textView.setOnClickListener {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("guideMode", true).apply()
            findNavController.navigate(item.navId)
        }
        holder.textView.text =  context.resources.getString(item.stringResourceId)
    }

    override fun getItemCount() = dataSet.size
}