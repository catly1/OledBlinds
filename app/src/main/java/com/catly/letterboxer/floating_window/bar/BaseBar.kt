package com.catly.letterboxer.floating_window.bar

import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import androidx.preference.PreferenceManager
import com.catly.letterboxer.floating_window.FloatingWindowService
import com.catly.letterboxer.utils.Utils

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
    private var added = false

    init{
        handleTapBehind()
        applyOpacity()
    }

    fun update(){
        if (!added) return
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
        // The delayed hide would otherwise stay queued on a view that is no longer on screen.
        if (this::hideRunnable.isInitialized){
            viewLayout.removeCallbacks(hideRunnable)
        }
        if (!added) return
        added = false
        try {
            windowManager.removeView(viewLayout)
        } catch (e: IllegalArgumentException) {
            // The window token already died with the display; there is nothing left to take down.
        }
    }

    fun attach(){
        if (added) return
        // Tracked by hand: View.isAttachedToWindow only becomes true on the first frame traversal,
        // so a start followed immediately by a stop would skip removeView and strand the overlay.
        windowManager.addView(viewLayout,param)
        added = true
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
        // The passthrough ceiling below depends on this flag, so the alpha has to be recomputed.
        applyOpacity()
        update()
    }

    /**
     * Window level alpha rather than a translucent background colour: the surface is blended by the
     * compositor, so changing it costs no extra drawing.
     */
    fun applyOpacity(){
        val alpha = Utils.barAlpha(sharedPreferences)
        // Android 12 refuses to deliver touches to the app underneath an untrusted overlay that is
        // more opaque than the system threshold, which would make touch passthrough do nothing.
        param.alpha = if (floatingWindowService.tapBehind) {
            minOf(alpha, Utils.maxPassThroughAlpha(context))
        } else {
            alpha
        }
    }

    open fun applyOpacityAndUpdate(){
        applyOpacity()
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
