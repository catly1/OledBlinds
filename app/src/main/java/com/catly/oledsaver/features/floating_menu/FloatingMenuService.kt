package com.catly.oledsaver.features.floating_menu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.media.Image
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import androidx.preference.PreferenceManager
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import com.catly.oledsaver.R
import com.catly.oledsaver.features.main.MainActivity

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
    private lateinit var topLockButton: ImageButton
    private lateinit var leftLockButton: ImageButton
    private var width: Int = 0
    private var height: Int = 0
    private var locked = false
    private val lockedIcon = R.drawable.baseline_lock_white_24dp
    private val unlockedIcon = R.drawable.baseline_lock_open_white_24dp
    var override = false
    var isActive = false
    var statusBarSize = 0
    lateinit var overrideButton: ImageButton

    companion object {
        fun startService(context: Context){
            val startIntent = Intent(context, FloatingMenuService::class.java)
            context.startForegroundService(startIntent)
        }

        fun stopService(context: Context){
            val stopIntent = Intent(context, FloatingMenuService::class.java)
            context.stopService(stopIntent)
        }

        var isRunning = false
    }

    private var topHideRunnable: Runnable = Runnable {
        topCloseButton.visibility = View.GONE
        topRotateButton.visibility = View.GONE
        topLockButton.visibility = View.GONE
    }
    private var bottomHideRunnable: Runnable = Runnable {
        bottomResizeButton.visibility = View.GONE }

    private var leftHideRunnable: Runnable = Runnable {
        leftCloseButton.visibility = View.GONE
        leftRotateButton.visibility = View.GONE
        leftLockButton.visibility = View.GONE
    }
    private var rightHideRunnable: Runnable = Runnable {
        rightResizeButton.visibility = View.GONE }

    private var flipped = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener(){ sharedPreferences: SharedPreferences, key : String->
        when (key) {
            "override"->{
                override = sharedPreferences.getBoolean(key, false)
                if (isActive){
                    refresh()
                }
            }
            "isActive"->{
                isActive = sharedPreferences.getBoolean(key, false)
            }
        }
    }

    private fun refresh() {
        if (flipped) {
            removeLeftRight()
            leftRightMode()
        } else {
            removeTopBottom()
            topDownMode()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0)

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("OLED Blinds")
            .setContentText("OLED Blinds is running.")
            .setSmallIcon(R.drawable.ic_stat_oledsaver)
            .setContentIntent(pendingIntent)
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

        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isActive", true).apply()
        isActive = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isActive", false)
        override = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("override", false)
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        flipped = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isFlipped", false)
        locked = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isLocked", false)
        setWidthHeightValues()
        if (flipped){
            leftRightMode()
        } else {
            topDownMode()
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceListener)
        isRunning = true
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
        createBottomBar()
        createTopBar()
        addTopBottomViews()
        hideTopBottomButtons()
        manageTopBottomVisibility()
    }

    private fun addTopBottomViews(){
        mWindowManager.addView(topBarView, topParam)
        mWindowManager.addView(bottomBarView, bottomParam)
    }

    private fun leftRightMode(){
        createRightBar()
        createLeftBar()
        addLeftRightViews()
        hideLeftRightButtons()
        manageLeftRightVisibility()
    }

    private fun addLeftRightViews(){
        mWindowManager.addView(leftBarView,leftParam)
        mWindowManager.addView(rightBarView,rightParam)
    }

//    private fun calcLeftRightDimensions() : WindowManager.LayoutParams {
//        val param =
//
//        return param
//    }

    private fun createRightBar(){
        rightBarView = LayoutInflater.from(this).inflate(R.layout.right_bar, null)
        rightParam = WindowManager.LayoutParams(width,
            MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        rightParam.gravity = Gravity.RIGHT
        if (mWindowManager.defaultDisplay.rotation == Surface.ROTATION_270){
            rightParam.x = -92
        }
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
                                updateLeftRight()
                            }
//                            println("left: " + leftParam.x)
//                            println("right: " + rightParam.x)
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
        overrideButton = rightBarView.findViewById<ImageButton>(R.id.override_button).also {
            it.setOnTouchListener(object : View.OnTouchListener {
                var initialX: Int = 0
                var initialTouchX: Float = 0.toFloat()
                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = rightParam.x
                            initialTouchX = event.rawX
//                            stopLeftRightHideRunnables()
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            rightParam.x = (initialX - (event.rawX - initialTouchX)).toInt()
                            updateLeftRight()
//                            println("left: " + leftParam.x)
//                            println("right: " + rightParam.x)
                            return true
                        }
                        MotionEvent.ACTION_UP ->{
//                            PreferenceManager.getDefaultSharedPreferences(this@FloatingMenuService).edit().putInt("width", rightParam.width).apply()
                            hideLeftRightButtons()
                        }
                    }

                    return false
                }
            })
        }
    }

    fun updateLeftRight(){
        mWindowManager.updateViewLayout(leftBarView, leftParam)
        mWindowManager.updateViewLayout(rightBarView, rightParam)
    }

    private fun createLeftBar(){
        leftBarView = LayoutInflater.from(this).inflate(R.layout.left_bar, null)
        leftParam = WindowManager.LayoutParams(
            width,
            MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        leftParam.gravity = Gravity.LEFT
        if (mWindowManager.defaultDisplay.rotation == Surface.ROTATION_90){
            leftParam.x = -92
        }
        leftCloseButton = leftBarView.findViewById<ImageButton>(R.id.left_close_button).also {
            it.setOnClickListener {
                stopSelf()
            }
        }

        leftRotateButton = leftBarView.findViewById<ImageButton>(R.id.left_rotate_button).also {
            rotate(it)
        }

        leftLockButton = leftBarView.findViewById<ImageButton>(R.id.left_lock_button).also{
            handleLockIcon(it)
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
        topLockButton.visibility = View.VISIBLE
        bottomResizeButton.visibility = View.VISIBLE
    }

    private fun makeLeftRightButtonsVisible() {
        leftCloseButton.visibility = View.VISIBLE
        leftRotateButton.visibility = View.VISIBLE
        leftLockButton.visibility = View.VISIBLE
        rightResizeButton.visibility = View.VISIBLE
    }

    private fun calcDimensions() : WindowManager.LayoutParams{
        val param = WindowManager.LayoutParams(
            MATCH_PARENT,
                height,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT)
        if (override){
            param.width = mWindowManager.defaultDisplay.width + 184
        }
        return param
    }

    private fun createTopBar() {
        topBarView = LayoutInflater.from(this).inflate(R.layout.top_bar, null)
        topParam = calcDimensions()
        topParam.gravity = Gravity.TOP

        topCloseButton = topBarView.findViewById<ImageButton>(R.id.top_close_button).also {
                it.setOnClickListener {
                    stopSelf()
                }
        }

        topRotateButton = topBarView.findViewById<ImageButton>(R.id.top_rotate_button).also {
            rotate(it)
        }

        topLockButton = topBarView.findViewById<ImageButton>(R.id.top_lock_button).also {
            handleLockIcon(it)
        }
    }



    private fun handleLockIcon(imageButton: ImageButton) {
        if (locked) {
            imageButton.setImageResource(lockedIcon)
            lockButtons()
        } else {
            imageButton.setImageResource(unlockedIcon)
        }

        imageButton.setOnClickListener {
            locked = if (locked) {
                imageButton.setImageResource(unlockedIcon)
                unlockButtons()
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isLocked", false).apply()
                false
            } else {
                imageButton.setImageResource(lockedIcon)
                lockButtons()
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isLocked", true).apply()
                true
            }
        }
    }

    private fun lockButtons(){
        if (flipped){
            leftCloseButton.isEnabled = false
            leftRotateButton.isEnabled = false
            rightResizeButton.isEnabled = false
        } else {
            topCloseButton.isEnabled = false
            topRotateButton.isEnabled = false
            bottomResizeButton.isEnabled = false
        }
    }

    private fun unlockButtons(){
        if (flipped){
            leftCloseButton.isEnabled = true
            leftRotateButton.isEnabled = true
            rightResizeButton.isEnabled = true
        } else {
            topCloseButton.isEnabled = true
            topRotateButton.isEnabled = true
            bottomResizeButton.isEnabled = true
        }
    }


    private fun rotate(view: View){
            setWidthHeightValues()
            view.setOnClickListener {
                flipped = if (flipped) {
                    removeLeftRight()
                    topDownMode()
                    PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean("isFlipped", false).apply()
                    false
                } else {
                    removeTopBottom()
                    leftRightMode()
                    PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean("isFlipped", true).apply()
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
        bottomParam = calcDimensions()
        bottomParam.gravity = Gravity.BOTTOM
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        refresh()
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            handleCutoutOnRotation(mWindowManager.defaultDisplay.rotation)
//        }
    }

//    fun handleCutoutOnRotation(rotation: Int) {
//        when (rotation){
//            Surface.ROTATION_90 -> adjustLeftBar()
////            Surface.ROTATION_180 -> println("rotation is : 180")
//            Surface.ROTATION_270 -> adjustRightBar()
//        }
//    }
//
//    fun adjustRightBar(){
//        println("camera on right")
//        rightParam.x = -91
//    }
//
//    fun adjustLeftBar(){
//        println("camera on left")
//        leftParam.x = -80
//    }
}