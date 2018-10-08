package me.venko.presencetracker.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import me.venko.presencetracker.data.Location.DISTANCE_DECIMAL_FORMAT_SHORT
import me.venko.presencetracker.data.Location.KILOMETERS_FACTOR

/**
 * @author Victor Kosenko
 *
 */

fun newRadialBounds(center: LatLng, radius: Float): LatLngBounds {
    val distanceFromCenterToCorner = radius * Math.sqrt(2.0)
    val southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0)
    val northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0)
    return LatLngBounds(southwestCorner, northeastCorner)
}

/**
 * Calculates distance between two [Location] objects representing location coordinates.
 *
 * @param from First point
 * @param to   Second point
 * @return Distance between two specified points in meters
 */
fun evalDistanceInM(from: LatLng, to: LatLng): Double {
    return SphericalUtil.computeDistanceBetween(from, to)
}

private fun getWithFactor(distance: Double, factor: Double): Double {
    var distance = distance
    if (factor != 0.0) {
        distance /= factor
    }
    return distance
}

/**
 * Created printable representation of distance value with a specific format rules.
 *
 * @param distanceMeters Distance value in meters
 * @return Formatted distance string
 */
fun getPrintableDistanceInKm(distanceMeters: Double): String =
        DISTANCE_DECIMAL_FORMAT_SHORT.format(getWithFactor(distanceMeters, KILOMETERS_FACTOR.toDouble()))