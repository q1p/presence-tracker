package me.venko.presencetracker.data.tracker

import android.location.Location

/**
 * @author Victor Kosenko
 *
 */
data class LocationBounds(var center: Location, var radius: Float)