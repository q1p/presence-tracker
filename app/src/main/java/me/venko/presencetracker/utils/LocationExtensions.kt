package me.venko.presencetracker.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil

/**
 * @author Victor Kosenko
 *
 */

fun newRadialBounds(center: LatLng, radius: Float): LatLngBounds {
    val radiusDouble = radius.toDouble()
    return LatLngBounds.Builder()
            .include(SphericalUtil.computeOffset(center, radiusDouble, 0.0))
            .include(SphericalUtil.computeOffset(center, radiusDouble, 90.0))
            .include(SphericalUtil.computeOffset(center, radiusDouble, 180.0))
            .include(SphericalUtil.computeOffset(center, radiusDouble, 270.0))
            .build()
}