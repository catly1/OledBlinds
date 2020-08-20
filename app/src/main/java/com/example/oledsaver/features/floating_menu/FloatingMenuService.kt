package com.example.oledsaver.features.floating_menu

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.Button
import com.example.oledsaver.R
import com.example.oledsaver.features.main.MainActivity

class FloatingMenuService: Service() {
    private lateinit var mWindowManager:WindowManager
    private lateinit var floatingMenuView: View
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
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
        test.setOnTouchListener (object : View.OnTouchListener {
            var lastAction : Int = 0
            var initialX : Int = 0
            var initialY: Int = 0
            var initialTouchX : Float = 0.toFloat()
            var initialTouchY : Float = 0.toFloat()

            override fun onTouch(view: View, event: MotionEvent): Boolean{
                when(event.action){
                    MotionEvent.ACTION_DOWN -> {
                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y
                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.
                            val intent = Intent(this@FloatingMenuService, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            //close the service and remove the chat heads
                            stopSelf()
                        }
                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (event.getRawX() - initialTouchX) as Int
                        params.y = initialY + (event.getRawY() - initialTouchY) as Int
                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(floatingMenuView, params)
                        lastAction = event.action
                        return true
                    }
                }
                return false
            }
        }


        )

    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(floatingMenuView)
    }
}