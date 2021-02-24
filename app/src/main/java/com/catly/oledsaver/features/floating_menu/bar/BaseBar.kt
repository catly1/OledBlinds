package com.catly.oledsaver.features.floating_menu.bar

import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.preference.PreferenceManager
import com.catly.oledsaver.features.floating_menu.FloatingMenuService

open class BaseBar(floatingMenuService: FloatingMenuService) {
    val param = WindowManager.LayoutParams(
        0,
        0,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    )

    val context: Context = floatingMenuService.baseContext
    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val windowManager = floatingMenuService.getSystemService(Service.WINDOW_SERVICE) as WindowManager
    lateinit var viewLayout : View
    var TAG = ""

    fun update(){
        windowManager.updateViewLayout(viewLayout, param)
    }
}