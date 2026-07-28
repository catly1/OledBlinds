package com.catly.letterboxer.data.model

import org.json.JSONObject

/**
 * A user placed rectangle that covers a static UI element -- TikTok's like / comment / share /
 * profile column, a persistent search bar, a game HUD -- so that element cannot burn into the panel.
 *
 * Coordinates are absolute pixels measured from the top left of the display, which is exactly what an
 * overlay window with FLAG_LAYOUT_NO_LIMITS and Gravity.TOP or Gravity.LEFT uses. They only make
 * sense in the orientation they were placed in, hence [portrait]: a zone is attached only while the
 * screen orientation matches.
 */
data class OverlayZone(
    val id: Long,
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
    val portrait: Boolean
) {

    fun toJson(): JSONObject = JSONObject()
        .put(KEY_ID, id)
        .put(KEY_X, x)
        .put(KEY_Y, y)
        .put(KEY_WIDTH, width)
        .put(KEY_HEIGHT, height)
        .put(KEY_PORTRAIT, portrait)

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_X = "x"
        private const val KEY_Y = "y"
        private const val KEY_WIDTH = "w"
        private const val KEY_HEIGHT = "h"
        private const val KEY_PORTRAIT = "p"

        /** Returns null for an entry that cannot be read, so one bad record cannot lose the rest. */
        fun fromJson(json: JSONObject): OverlayZone? {
            val width = json.optInt(KEY_WIDTH, 0)
            val height = json.optInt(KEY_HEIGHT, 0)
            if (width <= 0 || height <= 0) return null
            return OverlayZone(
                id = json.optLong(KEY_ID, 0L),
                x = json.optInt(KEY_X, 0),
                y = json.optInt(KEY_Y, 0),
                width = width,
                height = height,
                portrait = json.optBoolean(KEY_PORTRAIT, true)
            )
        }
    }
}
