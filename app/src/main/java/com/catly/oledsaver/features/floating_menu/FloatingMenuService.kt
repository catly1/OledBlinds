package com.catly.oledsaver.features.floating_menu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import androidx.preference.PreferenceManager
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import com.catly.oledsaver.R

class FloatingMenuService : Service() {

    private val channelID = "OLED Blinds Service"
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
    private var width: Int = 0
    private var height: Int = 0

    companion object {
        fun startService(context: Context){
            val startIntent = Intent(context, FloatingMenuService::class.java)
            context.startForegroundService(startIntent)
        }

        fun stopService(context: Context){
            val stopIntent = Intent(context, FloatingMenuService::class.java)
            context.stopService(stopIntent)
        }
    }

    private var topHideRunnable: Runnable = Runnable {
        topCloseButton.visibility = View.GONE
        topRotateButton.visibility = View.GONE
    }
    private var bottomHideRunnable: Runnable = Runnable {
        bottomResizeButton.visibility = View.GONE }
    private var leftHideRunnable: Runnable = Runnable {
        leftCloseButton.visibility = View.GONE
        leftRotateButton.visibility = View.GONE
    }
    private var rightHideRunnable: Runnable = Runnable {
        rightResizeButton.visibility = View.GONE }
    private var flipped = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("OLED Blinds")
            .setContentText("OLED Blinds is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1,notification)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(channelID, "OLED Blinds Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }


    override fun onCreate() {
        super.onCreate()
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        flipped = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isFlipped", false)
        setWidthHeightValues()
        if (flipped){
            leftRightMode()
        } else {
            topDownMode()
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isActive", true).apply()
    }

    private fun setWidthHeightValues(){
        width = PreferenceManager.getDefaultSharedPreferences(this).getInt("width",200)
        width = if (checkIfValidNumber(width)){
            width
        } else {
            200
        }

        height = PreferenceManager.getDefaultSharedPreferences(this).getInt("height",200)
        height = if (checkIfValidNumber(height)){
            height
        } else {
            200
        }
    }

    private fun topDownMode(){
        createTopBar()
        createBottomBar()
        hideTopBottomButtons()
        manageTopBottomVisibility()
    }

    private fun leftRightMode(){
        createLeftBar()
        createRightBar()
        hideLeftRightButtons()
        manageLeftRightVisibility()
    }

    private fun createRightBar(){
        rightBarView = LayoutInflater.from(this).inflate(R.layout.right_bar, null)

        rightParam = WindowManager.LayoutParams(
            width,
            MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        rightParam.gravity = Gravity.RIGHT
        mWindowManager.addView(rightBarView,rightParam)

        rightResizeButton = rightBarView.findViewById<ImageButton>(R.id.right_resize_button).also {
            it.setOnTouchListener(object : View.OnTouchListener {
                var initialX: Int = 0
                var initialTouchX: Float = 0.toFloat()
                var initialWidth: Int = 0
                var calculatedWidth = 0

                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = rightParam.x
                            initialWidth = rightParam.width
                            initialTouchX = event.rawX
                            stopLeftRightHideRunnables()
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            calculatedWidth = (initialWidth - (event.rawX - initialTouchX)).toInt()
                            if (checkIfValidNumber(calculatedWidth)) {
                                leftParam.width = calculatedWidth
                                rightParam.width = leftParam.width
                                mWindowManager.updateViewLayout(leftBarView, leftParam)
                                mWindowManager.updateViewLayout(rightBarView, rightParam)
                            }
                            return true
                        }
                        MotionEvent.ACTION_UP ->{
                            PreferenceManager.getDefaultSharedPreferences(this@FloatingMenuService).edit().putInt("width", rightParam.width).apply()
                            hideLeftRightButtons()
                        }
                    }

                    return false
                }
            })
        }
    }

    private fun createLeftBar(){
        leftBarView = LayoutInflater.from(this).inflate(R.layout.left_bar, null)
        leftParam = WindowManager.LayoutParams(
            width,
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
        if (flipped) {
            removeLeftRight()
        } else {
            removeTopBottom()
        }
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

    private fun manageTopBottomVisibility() {
        topBarView.setOnClickListener {
            makeTopBottomButtonsVisible()
            hideTopBottomButtons()
        }

        bottomBarView.setOnClickListener {
            makeTopBottomButtonsVisible()
            hideTopBottomButtons()
        }
    }

    private fun manageLeftRightVisibility() {
        leftBarView.setOnClickListener {
            makeLeftRightButtonsVisible()
            hideLeftRightButtons()
        }

        rightBarView.setOnClickListener {
            makeLeftRightButtonsVisible()
            hideLeftRightButtons()
        }
    }

    private fun makeTopBottomButtonsVisible() {
        topCloseButton.visibility = View.VISIBLE
        topRotateButton.visibility = View.VISIBLE
        bottomResizeButton.visibility = View.VISIBLE
    }

    private fun makeLeftRightButtonsVisible() {
        leftCloseButton.visibility = View.VISIBLE
        leftRotateButton.visibility = View.VISIBLE
        rightResizeButton.visibility = View.VISIBLE
    }


    private fun createTopBar() {
        topBarView = LayoutInflater.from(this).inflate(R.layout.top_bar, null)
        topParam = WindowManager.LayoutParams(
            MATCH_PARENT,
            height,
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
        setWidthHeightValues()
        view.setOnClickListener {
            flipped = if (flipped) {
                removeLeftRight()
                topDownMode()
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isFlipped", false).apply()
                false
            } else {
                removeTopBottom()
                leftRightMode()
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isFlipped", true).apply()
                true
            }
        }
    }

    private fun hideTopBottomButtons() {
        topBarView.postDelayed(topHideRunnable, 3000)
        bottomBarView.postDelayed(bottomHideRunnable, 3000)
    }

    private fun hideLeftRightButtons(){
        leftBarView.postDelayed(leftHideRunnable, 3000)
        rightBarView.postDelayed(rightHideRunnable,3000)
    }

    private fun stopTopBottomHideRunnables(){
        bottomBarView.removeCallbacks(bottomHideRunnable)
        topBarView.removeCallbacks(topHideRunnable)
    }

    private fun stopLeftRightHideRunnables(){
        leftBarView.removeCallbacks(leftHideRunnable)
        rightBarView.removeCallbacks(rightHideRunnable)
    }

    private fun checkIfValidNumber(num: Int) : Boolean{
        return num > 60
    }

    private fun createBottomBar() {
        bottomBarView = LayoutInflater.from(this).inflate(R.layout.bottom_bar, null)
        bottomParam = WindowManager.LayoutParams(
            MATCH_PARENT,
            height,
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
                var calculatedHeight = 0

                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialY = bottomParam.y
                            initialHeight = bottomParam.height
                            initialTouchY = event.rawY
                            stopTopBottomHideRunnables()
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            calculatedHeight = (initialHeight - (event.rawY - initialTouchY)).toInt()
                            if (checkIfValidNumber(calculatedHeight)){
                                bottomParam.height = calculatedHeight
                                topParam.height = bottomParam.height
                                mWindowManager.updateViewLayout(topBarView, topParam)
                                mWindowManager.updateViewLayout(bottomBarView, bottomParam)
                            }
                            return true
                        }
                        MotionEvent.ACTION_UP ->{
                            PreferenceManager.getDefaultSharedPreferences(this@FloatingMenuService).edit().putInt("height", bottomParam.height).apply()
                            hideTopBottomButtons()
                        }
                    }

                    return false
                }
            })
        }
    }
}