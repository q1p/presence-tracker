package me.venko.presencetracker.data.tracker

import android.location.Location

/**
 * @author Victor Kosenko
 *
 */
data class LocationBounds(val center: Location, val radius: Float)