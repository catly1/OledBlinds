package com.catly.oledsaver.features.floating_menu.bar

import android.graphics.PixelFormat
import android.view.ViewGroup
import android.view.WindowManager

class BaseButtonsBar {
    val param = WindowManager.LayoutParams(
        0,
        0,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    )

    init {

    }
}