package me.venko.presencetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import me.venko.presencetracker.TrackerApplication
import me.venko.presencetracker.data.settings.SettingsRepository
import me.venko.presencetracker.data.tracker.PresenceState
import me.venko.presencetracker.data.tracker.WifiSsidTracker
import me.venko.presencetracker.utils.logd
import kotlin.coroutines.experimental.CoroutineContext


/**
 * @author Victor Kosenko
 */
class TrackerViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO // using IO dispatcher due settings access

    private val settings by lazy { SettingsRepository.instance }

    private var wifiSsidTracker: WifiSsidTracker? = null

    private var currentWifiSsid: String? = null

    private val _presenceState = MutableLiveData<PresenceState>()
            .apply {
                value = PresenceState.EVALUATING
            }
    val presenceState = _presenceState as LiveData<PresenceState>

    fun onLocationPermissionsGranted() {
        logd { "Permissions granted" }
        if (wifiSsidTracker == null) {
            wifiSsidTracker = WifiSsidTracker(
                    getApplication<TrackerApplication>().applicationContext,
                    onConnected = { s ->
                        currentWifiSsid = s
                        logd { "Wi-Fi connected: $s" }
                        updatePresenceState()
                    },
                    onDisconnected = {
                        currentWifiSsid = null
                        logd { "Wi-Fi disconnected" }
                        updatePresenceState()
                    }
            ).apply { register() }
        } else {
            updatePresenceState()
        }
    }

    fun onLocationPermissionsDenied() {
        logd { "Permissions declined" }
        _presenceState.value = PresenceState.NO_PERMISSIONS
    }

    private fun updatePresenceState() {
        launch {
            val wifiPresence = currentWifiSsid == settings.ssidName
            when {
                wifiPresence -> _presenceState.postValue(PresenceState.INSIDE_WIFI)
                else -> _presenceState.postValue(PresenceState.OUTSIDE)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wifiSsidTracker?.unregister()
    }
}
