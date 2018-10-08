package me.venko.presencetracker.data.tracker

import android.location.Location

/**
 * @author Victor Kosenko
 *
 */
data class LocationBounds(var center: Location, var radius: Float) {
    fun isEmpty(): Boolean {
        return (center.latitude == 0.0 && center.longitude == 0.0).and(radius == 0f)
    }
}