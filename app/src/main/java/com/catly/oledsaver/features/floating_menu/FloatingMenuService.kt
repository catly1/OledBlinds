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
import android.os.Build
import android.os.IBinder
import androidx.preference.PreferenceManager
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_menu.bar.BottomBar
import com.catly.oledsaver.features.floating_menu.bar.LeftBar
import com.catly.oledsaver.features.floating_menu.bar.RightBar
import com.catly.oledsaver.features.floating_menu.bar.TopBar
import com.catly.oledsaver.features.main.MainActivity

class FloatingMenuService : Service() {

    private lateinit var sharedpreferences: SharedPreferences
    private val channelID = "OLED Blinds Service"
    lateinit var windowManager: WindowManager
    private lateinit var topBarView: View
    private lateinit var bottomBarView: View
    private lateinit var leftBarView: View
    private lateinit var rightBarView: View
    private lateinit var topCloseButton: ImageButton
    private lateinit var leftCloseButton: ImageButton
    private lateinit var bottomResizeButton: ImageButton
    private lateinit var rightResizeButton: ImageButton
    private lateinit var topRotateButton: ImageButton
    private lateinit var leftRotateButton: ImageButton
    private lateinit var topLockButton: ImageButton
    private lateinit var leftLockButton: ImageButton
    var width: Int = 0
    var height: Int = 0
    var locked = false
    var override = false
    var isActive = false
    var statusBarSize = 0
    lateinit var overrideButton: ImageButton
    lateinit var leftBar: LeftBar
    lateinit var rightBar: RightBar
    lateinit var topBar: TopBar
    lateinit var bottomBar: BottomBar

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

    private fun getPrefValues(){
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedpreferences.edit().putBoolean("isActive", true).apply()
        isActive = sharedpreferences.getBoolean("isActive", false)
        override = sharedpreferences.getBoolean("override", false)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        flipped = sharedpreferences.getBoolean("isFlipped", false)
        locked = sharedpreferences.getBoolean("isLocked", false)
        statusBarSize = sharedpreferences.getString("statusBarSize", "92")!!.toInt()
    }

    override fun onCreate() {
        super.onCreate()
        getPrefValues()
        setWidthHeightValues()
        if (flipped){
            leftRightMode()
        } else {
            topDownMode()
        }
        setLockState()
        sharedpreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
        isRunning = true
    }

    private fun setLockState(){
        if (locked){
            lockButtons()
        }
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
        bottomBar = BottomBar(this)
        topBar = TopBar(this)
        windowManager.addView(bottomBar.viewLayout, bottomBar.param)
        windowManager.addView(topBar.viewLayout, topBar.param)
//        hideTopBottomButtons()
//        manageTopBottomVisibility()
    }

    private fun leftRightMode(){
        rightBar = RightBar(this)
        leftBar = LeftBar(this)
        windowManager.addView(leftBar.viewLayout,leftBar.param)
        windowManager.addView(rightBar.viewLayout,rightBar.param)
//        hideLeftRightButtons()
//        manageLeftRightVisibility()
    }

//    private fun createRightBar(){
//        rightBarView = LayoutInflater.from(this).inflate(R.layout.right_bar, null)
//        rightParam = WindowManager.LayoutParams(width,
//            MATCH_PARENT,
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            PixelFormat.TRANSLUCENT
//        )
//        rightParam.gravity = Gravity.RIGHT
//        if (windowManager.defaultDisplay.rotation == Surface.ROTATION_270 && override){
//            rightParam.x = -statusBarSize
//        }
//        rightResizeButton = rightBarView.findViewById<ImageButton>(R.id.right_resize_button).also {
//            it.setOnTouchListener(object : View.OnTouchListener {
//                var initialX: Int = 0
//                var initialTouchX: Float = 0.toFloat()
//                var initialWidth: Int = 0
//                var calculatedWidth = 0
//                override fun onTouch(view: View, event: MotionEvent): Boolean {
//                    when (event.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            initialX = rightParam.x
//                            initialWidth = rightParam.width
//                            initialTouchX = event.rawX
//                            stopLeftRightHideRunnables()
//                            return true
//                        }
//                        MotionEvent.ACTION_MOVE -> {
//                            calculatedWidth = (initialWidth - (event.rawX - initialTouchX)).toInt()
//                            if (checkIfValidNumber(calculatedWidth)) {
//                                leftParam.width = calculatedWidth
//                                rightParam.width = leftParam.width
//                                updateLeftRight()
//                            }
////                            println("left: " + leftParam.x)
////                            println("right: " + rightParam.x)
//                            return true
//                        }
//                        MotionEvent.ACTION_UP ->{
//                            PreferenceManager.getDefaultSharedPreferences(this@FloatingMenuService).edit().putInt("width", rightParam.width).apply()
//                            hideLeftRightButtons()
//                        }
//                    }
//
//                    return false
//                }
//            })
//        }
//        overrideButton = rightBarView.findViewById<ImageButton>(R.id.override_button).also {
//            it.setOnTouchListener(object : View.OnTouchListener {
//                var initialX: Int = 0
//                var initialTouchX: Float = 0.toFloat()
//                override fun onTouch(view: View, event: MotionEvent): Boolean {
//                    when (event.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            initialX = rightParam.x
//                            initialTouchX = event.rawX
////                            stopLeftRightHideRunnables()
//                            return true
//                        }
//                        MotionEvent.ACTION_MOVE -> {
//                            rightParam.x = (initialX - (event.rawX - initialTouchX)).toInt()
//                            updateLeftRight()
////                            println("left: " + leftParam.x)
////                            println("right: " + rightParam.x)
//                            return true
//                        }
//                        MotionEvent.ACTION_UP ->{
////                            PreferenceManager.getDefaultSharedPreferences(this@FloatingMenuService).edit().putInt("width", rightParam.width).apply()
//                            hideLeftRightButtons()
//                        }
//                    }
//
//                    return false
//                }
//            })
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        if (flipped) {
            removeLeftRight()
        } else {
            removeTopBottom()
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isActive", false).apply()
        isActive = false
    }

    private fun removeTopBottom(){
        windowManager.removeView(bottomBar.viewLayout)
        windowManager.removeView(topBar.viewLayout)
    }

    private fun removeLeftRight(){
        windowManager.removeView(leftBar.viewLayout)
        windowManager.removeView(rightBar.viewLayout)
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
            param.width = windowManager.defaultDisplay.width + statusBarSize*2
        }
        return param
    }

    fun lockButtons(){
        if (flipped){
            leftBar.lockButtons()
            rightBar.lockButtons()
        } else {
            bottomBar.lockButtons()
            topBar.lockButtons()
        }
    }

    fun unlockButtons(){
        if (flipped){
            leftBar.unlockButtons()
            rightBar.unlockButtons()
        } else {
            topBar.unlockButtons()
            bottomBar.unlockButtons()
        }
    }


    fun rotate(){
        setWidthHeightValues()
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        refresh()
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            handleCutoutOnRotation(mWindowManager.defaultDisplay.rotation)
//        }
    }

    fun showButtons() {
        if (flipped){
            leftBar.showButtons()
            leftBar.hideButtons()
            rightBar.showButtons()
            rightBar.hideButtons()
        } else {
            topBar.showButtons()
            topBar.hideButtons()
            bottomBar.showButtons()
            bottomBar.hideButtons()
        }
    }
}