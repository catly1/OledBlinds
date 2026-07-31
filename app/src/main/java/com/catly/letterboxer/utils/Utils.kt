package com.catly.letterboxer.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import android.hardware.input.InputManager
import android.os.Build
import android.view.WindowManager

object Utils {
    const val OPACITY_KEY = "barOpacity"
    const val ZONE_OPACITY_KEY = "zoneOpacity"
    const val ZONES_ENABLED_KEY = "zonesEnabled"
    const val ZONES_BLOCK_TOUCHES_KEY = "zonesBlockTouches"

    /**
     * A fully transparent overlay would still sit on screen while being invisible, leaving no way to
     * find it again, so the sliders bottom out here instead of at zero.
     */
    const val MIN_OPACITY = 10
    const val DEFAULT_OPACITY = 100

    /** Zones exist to be tapped through, so they default below the touch obscuring threshold. */
    const val DEFAULT_ZONE_OPACITY = 75

    /** Smallest bar thickness that still leaves the drag handle usable. */
    const val MIN_BAR_SIZE = 61

    /** A bar past this share of the screen would bury its own controls off screen. */
    private const val MAX_BAR_SIZE_FRACTION = 0.8f

    /** Used when the platform will not say; the documented default is 0.8. */
    private const val FALLBACK_PASS_THROUGH_ALPHA = 0.79f

    fun checkIfValidNumber(num: Int) : Boolean{
        return num > MIN_BAR_SIZE - 1
    }

    /**
     * The old check only had a floor, so a resize drag could grow a bar without limit until it
     * covered the screen and pushed its own handle out of reach.
     */
    fun clampBarSize(value: Int, screenExtent: Int): Int {
        val max = (screenExtent * MAX_BAR_SIZE_FRACTION).toInt().coerceAtLeast(MIN_BAR_SIZE)
        return value.coerceIn(MIN_BAR_SIZE, max)
    }

    /** Stored 10..100 percent, applied as a 0f..1f window alpha. */
    fun barAlpha(sharedPreferences: SharedPreferences): Float =
        alphaFor(sharedPreferences, OPACITY_KEY, DEFAULT_OPACITY)

    fun zoneAlpha(sharedPreferences: SharedPreferences): Float =
        alphaFor(sharedPreferences, ZONE_OPACITY_KEY, DEFAULT_ZONE_OPACITY)

    private fun alphaFor(sharedPreferences: SharedPreferences, key: String, default: Int): Float {
        val percent = sharedPreferences.getInt(key, default)
            .coerceIn(MIN_OPACITY, DEFAULT_OPACITY)
        return percent / 100f
    }

    /**
     * Android 12 stops delivering touches to whatever is underneath an untrusted overlay once the
     * overlay is more opaque than this, so anything meant to be tapped through has to stay below it.
     */
    fun maxPassThroughAlpha(context: Context): Float {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return 1f
        val inputManager = context.getSystemService(InputManager::class.java)
            ?: return FALLBACK_PASS_THROUGH_ALPHA
        return (inputManager.maximumObscuringOpacityForTouch - 0.01f)
            .coerceIn(0f, 1f)
    }

    /**
     * The whole panel, not the area left over after the system bars: overlays are laid out with
     * FLAG_LAYOUT_NO_LIMITS and are positioned against the physical top left corner.
     */
    fun realScreenSize(windowManager: WindowManager): Point {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.maximumWindowMetrics.bounds
            return Point(bounds.width(), bounds.height())
        }
        val point = Point()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealSize(point)
        return point
    }
}
