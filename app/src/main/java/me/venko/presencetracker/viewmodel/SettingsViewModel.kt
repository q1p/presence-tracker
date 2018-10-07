package me.venko.presencetracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import me.venko.presencetracker.data.settings.SettingsRepository
import me.venko.presencetracker.data.tracker.LocationBounds
import me.venko.presencetracker.utils.logd
import kotlin.coroutines.experimental.CoroutineContext

/**
 * @author Victor Kosenko
 *
 */
class SettingsViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private val settings by lazy { SettingsRepository.instance }

    val ssidName: LiveData<String> by lazy {
        MutableLiveData<String>().apply { loadSsid(this) }
    }

    val locationBounds: LiveData<LocationBounds> by lazy {
        MutableLiveData<LocationBounds>().apply { loadLocationBounds(this) }
    }

    private fun loadSsid(data: MutableLiveData<String>) {
        launch {
            data.postValue(settings.ssidName)
        }
    }

    fun saveSsid(newSsid: String) {
        logd { "Saving new SSID name: $ssidName" }
        launch {
            settings.ssidName = newSsid
        }
    }

    private fun loadLocationBounds(data: MutableLiveData<LocationBounds>) {
        launch {
            LocationBounds(
                    center = settings.fencingPoint,
                    radius = settings.fencingRadius
            ).let { data.postValue(it) }
        }
    }

    fun saveLocationBounds(locationBounds: LocationBounds) {
        logd { "Saving new location bound $locationBounds" }
        launch {
            settings.fencingPoint = locationBounds.center
            settings.fencingRadius = locationBounds.radius
        }
    }
}