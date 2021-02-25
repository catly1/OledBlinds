package com.catly.oledsaver.features.floating_window

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
import androidx.core.app.NotificationCompat
import com.catly.oledsaver.R
import com.catly.oledsaver.features.floating_window.bar.BottomBar
import com.catly.oledsaver.features.floating_window.bar.LeftBar
import com.catly.oledsaver.features.floating_window.bar.RightBar
import com.catly.oledsaver.features.floating_window.bar.TopBar
import com.catly.oledsaver.features.main.MainActivity

class FloatingWindowService : Service() {

    private lateinit var sharedpreferences: SharedPreferences
    private val channelID = "OLED Blinds Service"
    lateinit var windowManager: WindowManager
    lateinit var leftBar: LeftBar
    lateinit var rightBar: RightBar
    lateinit var topBar: TopBar
    lateinit var bottomBar: BottomBar
    var width: Int = 0
    var overrideWidth: Int = 0
    var height: Int = 0
    var overrideHeight: Int = 0
    var locked = false
    var override = false
    var isActive = false
    var statusBarSize = 0

    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, FloatingWindowService::class.java)
            context.startForegroundService(startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, FloatingWindowService::class.java)
            context.stopService(stopIntent)
        }

        var isRunning = false
    }

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
        handleOverrideDimensions()
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

    private fun handleOverrideDimensions(){
        overrideWidth = if (override){
            windowManager.defaultDisplay.width + statusBarSize * 2
        } else {
            MATCH_PARENT
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
    }

    private fun leftRightMode(){
        rightBar = RightBar(this)
        leftBar = LeftBar(this)
        windowManager.addView(leftBar.viewLayout,leftBar.param)
        windowManager.addView(rightBar.viewLayout,rightBar.param)
    }

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
            showLeftRightButtons()
            hideLeftRightButtons()
        } else {
            showTopBottomButtons()
            hideLeftRightButtons()
        }
    }

    fun showLeftRightButtons(){
        leftBar.showButtons()
        rightBar.showButtons()
    }

    fun hideLeftRightButtons(){
        leftBar.hideButtons()
        rightBar.hideButtons()
    }

    fun showTopBottomButtons(){
        topBar.showButtons()
        bottomBar.showButtons()
    }

    fun hideTopBottomButtons(){
        topBar.hideButtons()
        bottomBar.hideButtons()
    }
}