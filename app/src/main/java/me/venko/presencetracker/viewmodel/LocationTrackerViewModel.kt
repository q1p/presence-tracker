package me.venko.presencetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import me.venko.presencetracker.data.tracker.LocationTracker

/**
 * @author Victor Kosenko
 *

 */
class LocationTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val locationTracker: LocationTracker by lazy { LocationTracker(getApplication()) }

    val lastKnownLocation
        get() = locationTracker.lastKnownLocation

    fun onLocationPermissionsGranted() {
        locationTracker.register()
    }

    fun onLocationPermissionsDenied() {

    }

    override fun onCleared() {
        super.onCleared()
        locationTracker.unregister()
    }
}
