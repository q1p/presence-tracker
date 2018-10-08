package me.venko.presencetracker.data.tracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import me.venko.presencetracker.utils.logd
import me.venko.presencetracker.utils.logv

/**
 * @author Victor Kosenko
 *
 */
class LocationTracker(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val _lastKnownLocation: MutableLiveData<Location> = MutableLiveData()
    val lastKnownLocation = _lastKnownLocation as LiveData<Location>

    @SuppressLint("MissingPermission")
    fun register() {
        if (fusedLocationClient == null) {
            logv { "Registering tracker" }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context).apply {
                val locationRequest = LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = UPDATE_INTERVAL
                    fastestInterval = FASTEST_INTERVAL
                }
                requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }
    }

    fun unregister() {
        logv { "Unregistering tracker" }
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            onLocationChanged(result)
        }
    }

    private fun onLocationChanged(result: LocationResult?) {
        logd { "Current location update: ${result?.lastLocation}" }
        result?.let {
            _lastKnownLocation.value = result.lastLocation
        }
    }

    companion object {
        private const val UPDATE_INTERVAL: Long = 15 * 1000 //15 seconds
        private const val FASTEST_INTERVAL: Long = 5 * 1000 //5 seconds
    }
}