package com.catly.oledblinds.floating_window.bar

import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import androidx.preference.PreferenceManager
import com.catly.letterboxer.floating_window.FloatingWindowService

open class BaseBar(private val floatingWindowService: FloatingWindowService) {
    val param = WindowManager.LayoutParams(
        0,
        0,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    )
    val context: Context = floatingWindowService.baseContext
    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val windowManager = floatingWindowService.getSystemService(Service.WINDOW_SERVICE) as WindowManager
    lateinit var viewLayout : View
    lateinit var hideRunnable: Runnable
    lateinit var buttonsGroup: View
    var hideDuration: Long = 3000
    var TAG = ""

    init{
        handleTapBehind()
    }

    fun update(){
        windowManager.updateViewLayout(viewLayout, param)
    }

    fun hideButtons(){
        viewLayout.postDelayed(hideRunnable , hideDuration)
    }

    fun showButtons(){
        viewLayout.removeCallbacks(hideRunnable)
        buttonsGroup.visibility = View.VISIBLE
    }

    fun handleBarVisibility(floatingWindowService: FloatingWindowService){
        viewLayout.setOnClickListener {
            floatingWindowService.showButtons()
        }
    }

    fun remove(){
        windowManager.removeView(viewLayout)
    }

    fun attach(){
        windowManager.addView(viewLayout,param)
    }

    fun updateWidth(int: Int){
        param.width = (int)
        update()
    }

    fun revertX(){
        param.x = 0
        update()
    }

    fun adjustForCutoff(cutoff: Int){
        param.x = -cutoff
        update()
    }

    open fun handleTapBehindAndUpdate(){
        handleTapBehind()
        update()
    }

    open fun handleTapBehind(){
        if (floatingWindowService.tapBehind){
            param.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        } else {
            param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        }
    }

    open fun lockButtons(){
    }
}