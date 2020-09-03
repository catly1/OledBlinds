package com.example.oledsaver.features.floating_menu

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import com.example.oledsaver.R

class FloatingMenuService : Service() {

    private lateinit var mWindowManager: WindowManager
    private lateinit var topBarView: View
    private lateinit var bottomBarView: View
    private lateinit var topParam: WindowManager.LayoutParams
    private lateinit var bottomParam: WindowManager.LayoutParams
    private lateinit var topCloseButton: Button
    private lateinit var bottomResizeButton: Button

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createTopBar()
        createBottomBar()
        hideButtons()
        manageVisibility()
    }

    private fun manageVisibility() {
        topBarView.setOnClickListener {
            makeButtonsVisible()
            hideButtons()
        }

        bottomBarView.setOnClickListener {
            makeButtonsVisible()
            hideButtons()
        }
    }

    private fun makeButtonsVisible() {
        topCloseButton.visibility = View.VISIBLE
        bottomResizeButton.visibility = View.VISIBLE
    }

    private fun createTopBar() {
        topBarView = LayoutInflater.from(this).inflate(R.layout.top_bar, null)
        topParam = WindowManager.LayoutParams(
            MATCH_PARENT,
            200,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        topParam.gravity = Gravity.TOP
        mWindowManager.addView(topBarView, topParam)
        topCloseButton = topBarView.findViewById<Button>(R.id.top_close_button).also {
            it.setOnClickListener {
                mWindowManager.removeView(bottomBarView)
                mWindowManager.removeView(topBarView)
                stopSelf()
            }
        }
    }

    private fun hideButtons() {
        topBarView.postDelayed(Runnable {
            topCloseButton.visibility = View.GONE
        }, 3000)
        bottomBarView.postDelayed({
            bottomResizeButton.visibility = View.GONE
        }, 3000)
    }

    private fun createBottomBar() {
        bottomBarView = LayoutInflater.from(this).inflate(R.layout.bottom_bar, null)
        bottomParam = WindowManager.LayoutParams(
            MATCH_PARENT,
            200,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        bottomParam.gravity = Gravity.BOTTOM
        mWindowManager.addView(bottomBarView, bottomParam)
        bottomResizeButton = bottomBarView.findViewById<Button>(R.id.bottom_resize_button).also {
            it.setOnTouchListener(object : View.OnTouchListener {
                var initialY: Int = 0
                var initialTouchY: Float = 0.toFloat()
                var initialHeight: Int = 0

                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialY = bottomParam.y
                            initialHeight = bottomParam.height
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            topParam.height = (initialHeight - (event.rawY - initialTouchY)).toInt()
                            bottomParam.height =
                                (initialHeight - (event.rawY - initialTouchY)).toInt()
                            mWindowManager.updateViewLayout(topBarView, topParam)
                            mWindowManager.updateViewLayout(bottomBarView, bottomParam)
                            return true
                        }
                    }

                    return false
                }
            })
        }
    }
}