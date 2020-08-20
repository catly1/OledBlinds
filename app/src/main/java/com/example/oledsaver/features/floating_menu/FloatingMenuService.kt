package com.example.oledsaver.features.floating_menu

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.example.oledsaver.R

class FloatingMenuService: Service() {
    private lateinit var mWindowManager:WindowManager
    private lateinit var floatingMenuView: View
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //Inflate the chat head layout we created
        floatingMenuView = LayoutInflater.from(this).inflate(R.layout.floating_menu, null)
        //Add the view to the window.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)
        //Specify the chat head position
        //Initially view will be added to top-left corner
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100
        //Add the view to the window
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(floatingMenuView, params)

        val closeButton = floatingMenuView.findViewById<Button>(R.id.float_close_button)
        closeButton.setOnClickListener {
            stopSelf()
        }

        val test = floatingMenuView.findViewById<Button>(R.id.float_button)
        test.setOnClickListener {
            var lastAction : Int
            var initialX : Int
            var initialY: Int
            var initialTouchX : Float
            var initialTouchY : Float


        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingMenuView != null) mWindowManager.removeView(floatingMenuView)
    }
}