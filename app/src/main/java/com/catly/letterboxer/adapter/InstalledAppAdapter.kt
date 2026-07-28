package com.catly.letterboxer.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.catly.letterboxer.R
import com.catly.letterboxer.data.model.InstalledApp
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException

/**
 * Icons are deliberately not loaded up front. `loadIcon` hands back an adaptive icon with two large
 * layers, so materialising a couple of hundred of them at once costs tens of megabytes and as many
 * binder round trips. Instead each bound row rasterises its icon once, off the main thread, into a
 * small bitmap kept in a bounded cache.
 */
class InstalledAppAdapter(
    context: Context,
    private val selected: MutableSet<String>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<InstalledAppAdapter.ItemViewHolder>() {

    var appList: List<InstalledApp> = emptyList()

    private val packageManager = context.packageManager
    private val iconSizePx = (40 * context.resources.displayMetrics.density).toInt().coerceAtLeast(1)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val iconExecutor = Executors.newFixedThreadPool(2)

    private val iconCache = object : LruCache<String, Bitmap>(
        ((Runtime.getRuntime().maxMemory() / 1024) / 16).toInt().coerceAtLeast(512)
    ) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val label: TextView = view.findViewById(R.id.app_label)
        val checkBox: CheckBox = view.findViewById(R.id.app_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.installed_app_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val app = appList[position]
        holder.label.text = app.label
        // No OnCheckedChangeListener anywhere: setting the state during a rebind would fire the
        // recycled row's listener and silently toggle an app the user never touched.
        holder.checkBox.isChecked = selected.contains(app.packageName)
        holder.itemView.setOnClickListener {
            val nowSelected = !selected.contains(app.packageName)
            if (nowSelected) selected.add(app.packageName) else selected.remove(app.packageName)
            holder.checkBox.isChecked = nowSelected
            onSelectionChanged()
        }
        bindIcon(holder, app.packageName)
    }

    override fun getItemCount() = appList.size

    fun update(fetchedList: List<InstalledApp>) {
        appList = fetchedList
        notifyDataSetChanged()
    }

    /** Stops the background rasterising once the screen goes away. */
    fun shutdown() {
        iconExecutor.shutdownNow()
        mainHandler.removeCallbacksAndMessages(null)
        iconCache.evictAll()
    }

    private fun bindIcon(holder: ItemViewHolder, packageName: String) {
        // Tagged so a row recycled before its icon arrives does not show the previous app's icon.
        holder.icon.tag = packageName
        val cached = iconCache.get(packageName)
        if (cached != null) {
            holder.icon.setImageBitmap(cached)
            return
        }
        holder.icon.setImageDrawable(null)
        try {
            iconExecutor.execute {
                val bitmap = rasterizeIcon(packageName) ?: return@execute
                iconCache.put(packageName, bitmap)
                mainHandler.post {
                    if (holder.icon.tag == packageName) {
                        holder.icon.setImageBitmap(bitmap)
                    }
                }
            }
        } catch (e: RejectedExecutionException) {
            // The screen is going away; an icon less row is fine.
        }
    }

    private fun rasterizeIcon(packageName: String): Bitmap? = try {
        val drawable = packageManager.getApplicationIcon(packageName)
        val bitmap = Bitmap.createBitmap(iconSizePx, iconSizePx, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, iconSizePx, iconSizePx)
        drawable.draw(Canvas(bitmap))
        bitmap
    } catch (e: Exception) {
        null
    }
}
