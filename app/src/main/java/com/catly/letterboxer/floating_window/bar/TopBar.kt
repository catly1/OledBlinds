package com.catly.letterboxer.floating_window.bar

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import com.catly.letterboxer.R

import com.catly.letterboxer.floating_window.FloatingWindowService
import com.catly.letterboxer.utils.Utils

class TopBar(floatingWindowService: FloatingWindowService): BaseButtonsBar(floatingWindowService) {
    lateinit var resizeButton: ImageButton

    init {
        TAG = "TopBar"
        param.width = floatingWindowService.overrideWidthForTopBottom
        param.height = floatingWindowService.topBarHeight + 5
        param.y = -5
        param.gravity = Gravity.TOP
        viewLayout = LayoutInflater.from(context).inflate(R.layout.top_bar, null)
        resizeButton = viewLayout.findViewById<ImageButton>(R.id.top_resize_button)
        closeButton = viewLayout.findViewById(R.id.top_close_button)
        rotateButton = viewLayout.findViewById(R.id.top_rotate_button)
        lockButton = viewLayout.findViewById(R.id.top_lock_button)
        buttonsGroup = viewLayout.findViewById<View>(R.id.top_bar_buttons)
        hideRunnable = Runnable {
            buttonsGroup.visibility = View.GONE
        }
        setListeners()
        setLockIconFromPrefs(lockButton)
        hideButtons()
        handleBarVisibility(floatingWindowService)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setListeners() {
        super.setListeners()
        val maxHeight = Utils.realScreenSize(windowManager).y

        resizeButton.setOnTouchListener(object : View.OnTouchListener {
            var initialTouchY: Float = 0f
            var initialHeight: Int = 0
            var calculatedHeight = 0

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialHeight = param.height - 5
                        initialTouchY = event.rawY
                        floatingWindowService.showTopBottomButtons()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        calculatedHeight = Utils.clampBarSize(
                            (initialHeight + (event.rawY - initialTouchY)).toInt(),
                            maxHeight
                        )
                        param.height = calculatedHeight + 5
                        floatingWindowService.topBarHeight = calculatedHeight
                        val link = sharedPreferences.getBoolean("linkTopBottomBars", true)
                        if (link && floatingWindowService.viewsAttached && !floatingWindowService.flipped) {
                            floatingWindowService.bottomBarHeight = calculatedHeight
                            floatingWindowService.bottomBar.param.height = calculatedHeight
                            floatingWindowService.bottomBar.update()
                        }
                        update()
                        return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        val heightVal = param.height - 5
                        sharedPreferences.edit()
                            .putInt("topBarHeight", heightVal)
                            .putInt("height", heightVal)
                            .apply()
                        val link = sharedPreferences.getBoolean("linkTopBottomBars", true)
                        if (link) {
                            sharedPreferences.edit().putInt("bottomBarHeight", heightVal).apply()
                        }
                        floatingWindowService.hideTopBottomButtons()
                    }
                }
                return false
            }
        })
    }

    override fun lockButtons() {
        resizeButton.isEnabled = false
        super.lockButtons()
    }

    override fun unlockButtons() {
        resizeButton.isEnabled = true
        super.unlockButtons()
    }
}