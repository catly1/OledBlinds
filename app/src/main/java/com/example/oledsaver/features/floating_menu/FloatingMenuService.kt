package com.example.oledsaver.features.floating_menu

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.oledsaver.R
import com.example.oledsaver.features.main.MainActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class FloatingMenuService: Service() {

    private lateinit var mWindowManager:WindowManager
    private lateinit var floatingMenuView: View
    private lateinit var topBarView: View
    private lateinit var bottomBarView: View
    private val views = ArrayList<View>()
    private val params = ArrayList<WindowManager.LayoutParams>()
    private lateinit var topParam : WindowManager.LayoutParams
    private lateinit var bottomParam : WindowManager.LayoutParams
    private lateinit var topCloseButton: Button
    private lateinit var bottomResizeButton: Button


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        //Inflate the chat head layout we created

        //Add the view to the window.

//        createParamsAndView()
//        createParamsAndView()
        createTopBar()
        createBottomBar()
        hideButtons()
        manageVisibility()
        //Add the view to the window

    }

    private fun manageVisibility(){
        topBarView.setOnClickListener {
            topCloseButton.visibility = View.VISIBLE
            bottomResizeButton.visibility = View.VISIBLE
            hideButtons()
        }

        bottomBarView.setOnClickListener {
            topCloseButton.visibility = View.VISIBLE
            bottomResizeButton.visibility = View.VISIBLE
            hideButtons()
        }
    }

    private fun createTopBar(){
        topBarView = LayoutInflater.from(this).inflate(R.layout.top_bar, null)
        topParam = WindowManager.LayoutParams(
            MATCH_PARENT,
            200,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        topParam.gravity = Gravity.TOP
        mWindowManager.addView(topBarView,topParam)
        topCloseButton = topBarView.findViewById<Button>(R.id.top_close_button).also {
            it.setOnClickListener {
                mWindowManager.removeView(bottomBarView)
                mWindowManager.removeView(topBarView)
                stopSelf()
            }
        }
    }

    private fun hideButtons(){
        topBarView.postDelayed(Runnable {
            topCloseButton.visibility = View.GONE
        },3000)
        bottomBarView.postDelayed({
            bottomResizeButton.visibility = View.GONE
        },3000)
    }

    private fun createBottomBar(){
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
                            //                    //remember the initial position.
                            initialY = bottomParam.y
                            //                    //remember initial dimensions.
                            initialHeight = bottomParam.height
                            //                    //get the touch location
                            initialTouchY = event.rawY
                            Toast.makeText(application,"it clicks", Toast.LENGTH_SHORT).show()
                            //                    lastAction = event.action
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            topParam.height = (initialHeight - (event.rawY - initialTouchY)).toInt()
                            bottomParam.height = (initialHeight - (event.rawY - initialTouchY)).toInt()
//                            bottomParam.y = (initialY + (event.rawY - initialTouchY)).toInt()
//                            param.width = (initialWidth  + (event.rawX - initialTouchX)).toInt()
                            mWindowManager.updateViewLayout(topBarView, topParam)
                            mWindowManager.updateViewLayout(bottomBarView,bottomParam)
                            return true
                        }
                    }

                    return false
                }
            })
        }
    }


    private fun createParamsAndView(){
        floatingMenuView = LayoutInflater.from(this).inflate(R.layout.floating_menu, null)
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
        param.height = 500
        param.width = 500

        params.add(param)
        views.add(floatingMenuView)
        mWindowManager.addView(floatingMenuView, param)
        setTouchEvents(floatingMenuView, param)
    }

    private fun setTouchEvents(floatingMenuView: View, params: WindowManager.LayoutParams) {
        floatingMenuView.findViewById<Button>(R.id.float_close_button).also {
            it.setOnClickListener {
                views.remove(floatingMenuView)
                mWindowManager.removeView(floatingMenuView)
                if (views.size == 0) {
                    stopSelf()
                }
            }
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

        floatingMenuView.findViewById<Button>(R.id.save_button).also {
            it.setOnClickListener { v: View ->

            }
        }
    }


}