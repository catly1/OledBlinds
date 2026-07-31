package com.catly.letterboxer.data

import android.content.SharedPreferences
import com.catly.letterboxer.data.model.OverlayZone
import org.json.JSONArray
import org.json.JSONException

/**
 * Stores the custom zones as a JSON array in the default SharedPreferences. org.json ships with the
 * framework, so this needs no serialisation dependency.
 */
object ZoneRepository {

    const val KEY = "customZones"

    /**
     * Each zone is its own overlay window. Devices have a small budget of hardware composition
     * planes, already partly spent on the app, the system bars and the two letterbox bars, so past a
     * handful of zones the whole screen falls back to GPU composition every frame. The use this
     * exists for needs one or two.
     */
    const val MAX_ZONES = 8

    fun load(sharedPreferences: SharedPreferences): MutableList<OverlayZone> {
        val raw = sharedPreferences.getString(KEY, null) ?: return mutableListOf()
        val zones = mutableListOf<OverlayZone>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val json = array.optJSONObject(i) ?: continue
                OverlayZone.fromJson(json)?.let { zones.add(it) }
            }
        } catch (e: JSONException) {
            // Corrupt preference value: start over rather than crash the service on every launch.
            return mutableListOf()
        }
        return zones
    }

    fun save(sharedPreferences: SharedPreferences, zones: List<OverlayZone>) {
        val array = JSONArray()
        zones.forEach { array.put(it.toJson()) }
        sharedPreferences.edit().putString(KEY, array.toString()).apply()
    }

    fun nextId(zones: List<OverlayZone>): Long = (zones.maxOfOrNull { it.id } ?: 0L) + 1L
}
