package me.venko.presencetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import me.venko.presencetracker.data.settings.SettingsRepository
import me.venko.presencetracker.data.tracker.*
import me.venko.presencetracker.utils.logd
import kotlin.coroutines.experimental.CoroutineContext


/**
 * @author Victor Kosenko
 */
class PresenceTrackerViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO // using IO dispatcher due settings access

    private val settings by lazy { SettingsRepository.instance }

    private var wifiSsidTracker: WifiSsidTracker? = null
    private var geofencesTracker: GeofencesTracker? = null

    private var currentWifiSsid: String? = null
    private var geofencingStatus = GeofencingStatus.UNKNOWN

    private val _presenceState = MutableLiveData<PresenceState>()
            .apply {
                value = PresenceState.EVALUATING
            }
    val presenceState = _presenceState as LiveData<PresenceState>

    fun onLocationPermissionsGranted() {
        logd { "Permissions granted" }

        wifiSsidTracker?.apply {
            updatePresenceState()
        } ?: run {
            wifiSsidTracker = WifiSsidTracker(
                    getApplication(),
                    onConnected = { newSsid ->
                        currentWifiSsid = newSsid
                        logd { "Wi-Fi connected: $newSsid" }
                        updatePresenceState()
                    },
                    onDisconnected = {
                        currentWifiSsid = null
                        logd { "Wi-Fi disconnected" }
                        updatePresenceState()
                    }
            ).apply { register() }
        }

        geofencesTracker?.apply {
            launch {
                val locationBounds = makeLocationBounds()
                withContext(Dispatchers.Main) {
                    updateBounds(locationBounds)
                }
            }
            updatePresenceState()
        } ?: run {
            geofencesTracker = GeofencesTracker(getApplication(),
                    onStatusUpdate = { newStatus ->
                        geofencingStatus = newStatus
                        logd { "New geofencing status: $newStatus" }
                        updatePresenceState()
                    })
                    .apply {
                        launch {
                            val locationBounds = makeLocationBounds()
                            withContext(Dispatchers.Main) {
                                register(locationBounds)
                            }
                        }
                    }
        }
    }

    private fun makeLocationBounds(): LocationBounds = LocationBounds(
            center = settings.fencingPoint,
            radius = settings.fencingRadius
    )

    fun onLocationPermissionsDenied() {
        logd { "Permissions declined" }
        _presenceState.value = PresenceState.NO_PERMISSIONS
    }

    private fun updatePresenceState() {
        launch {
            val wifiPresence = currentWifiSsid == settings.ssidName
            val geoPresence = geofencingStatus == GeofencingStatus.INSIDE
            when {
                wifiPresence && geoPresence -> _presenceState.postValue(PresenceState.INSIDE_BOTH)
                wifiPresence -> _presenceState.postValue(PresenceState.INSIDE_WIFI)
                geoPresence -> _presenceState.postValue(PresenceState.INSIDE_GEO)
                else -> _presenceState.postValue(PresenceState.OUTSIDE)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wifiSsidTracker?.unregister()
        geofencesTracker?.unregister()
    }
}
