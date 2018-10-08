package me.venko.presencetracker.data.settings

import android.content.Context
import android.location.Location
import me.venko.presencetracker.TrackerApplication
import me.venko.presencetracker.utils.getDouble
import me.venko.presencetracker.utils.putDouble

/**
 * @author Victor Kosenko
 *
 */
class SettingsRepository private constructor() {

    private object Holder {
        val INSTANCE = SettingsRepository()
    }

    private val prefs by lazy {
        TrackerApplication.instance
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var ssidName: String
        get() = prefs.getString(PREF_SSID_NAME, "").orEmpty()
        set(value) {
            prefs.edit().putString(PREF_SSID_NAME, value).apply()
        }

    var fencingRadius
        get() = prefs.getFloat(PREF_FENCING_RADIUS, 0f)
        set(value) {
            prefs.edit().putFloat(PREF_FENCING_RADIUS, value).apply()
        }

    var fencingPoint: Location
        get() {
            return Location("").apply {
                latitude = prefs.getDouble(PREF_FENCING_POINT_LAT, 0.0)
                longitude = prefs.getDouble(PREF_FENCING_POINT_LON, 0.0)
            }
        }
        set(value) {
            prefs.edit()
                    .putDouble(PREF_FENCING_POINT_LAT, value.latitude)
                    .putDouble(PREF_FENCING_POINT_LON, value.longitude)
                    .apply()
        }

    companion object {
        private const val PREFS_NAME = "settings"

        private const val PREF_SSID_NAME = "ssidName"
        private const val PREF_FENCING_RADIUS = "fencingRadius"
        private const val PREF_FENCING_POINT_LAT = "fencingPointLat"
        private const val PREF_FENCING_POINT_LON = "fencingPointLon"

        val instance by lazy { Holder.INSTANCE }
    }
}