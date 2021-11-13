package com.catly.oledsaver.features.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController

import androidx.recyclerview.widget.RecyclerView
import com.catly.oledsaver.R
import com.catly.oledsaver.features.data.model.GuideIndexItem

class GuideIndexItemAdapter(
    private val context: Context,
    private val findNavController: NavController
) : RecyclerView.Adapter<GuideIndexItemAdapter.ItemViewHolder>() {

    var guideList: List<GuideIndexItem> = mutableListOf()

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.guide_index_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = guideList[position]
        holder.textView.setOnClickListener {
            val args = bundleOf("guideMode" to true)
            findNavController.navigate(item.navId, args)
        }
        holder.textView.text =  context.resources.getString(item.stringResourceId)
    }

    override fun getItemCount() = guideList.size

    fun update(fetchedList: List<GuideIndexItem>) {
        guideList = fetchedList
        notifyDataSetChanged()
    }
}