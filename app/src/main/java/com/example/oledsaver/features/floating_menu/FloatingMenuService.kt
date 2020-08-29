package com.example.oledsaver.features.floating_menu

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import com.example.oledsaver.R
import com.example.oledsaver.features.main.MainActivity

class FloatingMenuService: Service() {

    private lateinit var mWindowManager:WindowManager
    private lateinit var floatingMenuView: View
    private val displayMetrics = DisplayMetrics()
    private val params = ArrayList<WindowManager.LayoutParams>()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //Inflate the chat head layout we created
        floatingMenuView = LayoutInflater.from(this).inflate(R.layout.floating_menu, null)
        //Add the view to the window.

        createParams()

        //Add the view to the window
        params.forEach{param ->
            mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            mWindowManager.addView(floatingMenuView, param)
            setTouchEvents(floatingMenuView, param)
        }

    }


    private fun createParams(){
        val param = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        param.gravity = Gravity.TOP or Gravity.LEFT
        param.x = 0
        param.y = 100
        param.height = 200
        param.width = 200

        params.add(param)
    }

    private fun setTouchEvents(floatingMenuView: View, params: WindowManager.LayoutParams) {
        val closeButton = floatingMenuView.findViewById<Button>(R.id.float_close_button)
        closeButton.setOnClickListener {
            stopSelf()
        }

        floatingMenuView.findViewById<Button>(R.id.float_button).also {
            it.setOnTouchListener(object : View.OnTouchListener {
                var lastAction: Int = 0
                var initialX: Int = 0
                var initialY: Int = 0
                var initialTouchX: Float = 0.toFloat()
                var initialTouchY: Float = 0.toFloat()

                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
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
                            params.x = (initialX + (event.rawX - initialTouchX)).toInt()
                            params.y = (initialY + (event.rawY - initialTouchY)).toInt()
                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(floatingMenuView, params)
                            lastAction = event.action
                            return true
                        }
                    }
                    return false
                }
            })
        }

        floatingMenuView.findViewById<Button>(R.id.bottom_right_button).also {
                it.setOnTouchListener(object : View.OnTouchListener {
                    var lastAction: Int = 0
                    var initialX: Int = 0
                    var initialY: Int = 0
                    var initialTouchX: Float = 0.toFloat()
                    var initialTouchY: Float = 0.toFloat()
                    var initialHeight: Int = 0
                    var initialWidth: Int = 0

                    override fun onTouch(view: View, event: MotionEvent): Boolean {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                //                    //remember the initial position.
                                initialX = params.x
                                initialY = params.y
                                //                    //remember initial dimensions.
                                initialHeight = params.height
                                initialWidth = params.width
                                //                    //get the touch location
                                initialTouchX = event.rawX
                                initialTouchY = event.rawY
                                //                    lastAction = event.action
                                return true
                            }
                            MotionEvent.ACTION_MOVE -> {
                                params.height = (initialHeight + (event.rawY - initialTouchY)).toInt()
                                params.width = (initialWidth  + (event.rawX - initialTouchX)).toInt()
                                mWindowManager.updateViewLayout(floatingMenuView, params)
                                return true
                            }
                        }

                        return false
                    }
                })
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(floatingMenuView)
    }
}