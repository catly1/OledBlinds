package com.catly.oledsaver.features.floating_menu

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.Image
import android.os.IBinder
import androidx.preference.PreferenceManager
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import com.catly.oledsaver.R

class FloatingMenuService : Service() {

    private lateinit var mWindowManager: WindowManager
    private lateinit var topBarView: View
    private lateinit var bottomBarView: View
    private lateinit var leftBarView: View
    private lateinit var rightBarView: View
    private lateinit var topParam: WindowManager.LayoutParams
    private lateinit var bottomParam: WindowManager.LayoutParams
    private lateinit var leftParam: WindowManager.LayoutParams
    private lateinit var rightParam: WindowManager.LayoutParams
    private lateinit var topCloseButton: ImageButton
    private lateinit var leftCloseButton: ImageButton
    private lateinit var bottomResizeButton: ImageButton
    private lateinit var rightResizeButton: ImageButton
    private lateinit var topRotateButton: ImageButton
    private lateinit var leftRotateButton: ImageButton
    private var topHideRunnable: Runnable = Runnable {
        topCloseButton.visibility = View.GONE
        topRotateButton.visibility = View.GONE
    }
    private var bottomHideRunnable: Runnable = Runnable { bottomResizeButton.visibility = View.GONE }
    private var flipped = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        topDownMode()
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isActive", true).apply()
    }

    private fun topDownMode(){
        createTopBar()
        createBottomBar()
        hideButtons()
        manageVisibility()
    }

    private fun leftRightMode(){
        createLeftBar()
        createRightBar()
        hideButtons()
        manageVisibility()
    }

    private fun createRightBar(){
        rightBarView = LayoutInflater.from(this).inflate(R.layout.right_bar, null)
        rightParam = WindowManager.LayoutParams(
            200,
            MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        rightParam.gravity = Gravity.RIGHT
        mWindowManager.addView(rightBarView,rightParam)
    }

    private fun createLeftBar(){
        leftBarView = LayoutInflater.from(this).inflate(R.layout.left_bar, null)
        leftParam = WindowManager.LayoutParams(
            200,
            MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        leftParam.gravity = Gravity.LEFT
        mWindowManager.addView(leftBarView,leftParam)

        leftCloseButton = leftBarView.findViewById<ImageButton>(R.id.left_close_button).also {
            it.setOnClickListener {
                stopSelf()
            }
        }

        leftRotateButton = leftBarView.findViewById<ImageButton>(R.id.left_rotate_button).also {
            rotate(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        removeTopBottom()
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isActive", false).apply()
    }

    private fun removeTopBottom(){
        mWindowManager.removeView(bottomBarView)
        mWindowManager.removeView(topBarView)
    }

    private fun removeLeftRight(){
        mWindowManager.removeView(leftBarView)
        mWindowManager.removeView(rightBarView)
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
        topRotateButton.visibility = View.VISIBLE
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
        topCloseButton = topBarView.findViewById<ImageButton>(R.id.top_close_button).also {
            it.setOnClickListener {
                stopSelf()
            }
        }

        topRotateButton = topBarView.findViewById<ImageButton>(R.id.top_rotate_button).also {
            rotate(it)
        }
    }

    private fun rotate(view: View){
        view.setOnClickListener {
            flipped = if (flipped) {
                removeLeftRight()
                topDownMode()
                false
            } else {
                removeTopBottom()
                leftRightMode()
                true
            }
        }
    }

    private fun hideButtons() {
        topBarView.postDelayed(topHideRunnable, 3000)
        bottomBarView.postDelayed(bottomHideRunnable, 3000)
    }

    private fun stopHideRunnables(){
        bottomBarView.removeCallbacks(bottomHideRunnable)
        topBarView.removeCallbacks(topHideRunnable)
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
        bottomResizeButton = bottomBarView.findViewById<ImageButton>(R.id.bottom_resize_button).also {
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
                            stopHideRunnables()
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
                        MotionEvent.ACTION_UP ->{
                            hideButtons()
                        }
                    }

                    return false
                }
            })
        }
    }
}