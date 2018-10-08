package me.venko.presencetracker.data.tracker

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Handler
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import me.venko.presencetracker.data.events.GeofencingStatusUpdate
import me.venko.presencetracker.service.GeofencesTrackerIntentService
import me.venko.presencetracker.utils.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Victor Kosenko
 *
 */
class GeofencesTracker(
        private val context: Context,
        private val onStatusUpdate: (GeofencingStatus) -> Unit
) {

    private var geofencingClient: GeofencingClient? = null
    private var providersStatusReceiver: ProvidersStateReceiver? = null
    private var locationManager: LocationManager? = null
    private val bus = EventBus.getDefault()
    private var lastLocationBounds: LocationBounds? = null
    private var isGeofencingSet = false
    private val delayedGeofenceSetupHandler by lazy { Handler() }

    @SuppressLint("MissingPermission")
    fun register(locationBounds: LocationBounds) {
        lastLocationBounds = locationBounds
        bus.register(this)
        logv { "Registering tracker" }
        geofencingClient = LocationServices.getGeofencingClient(context)
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (providersStatusReceiver == null) {
            providersStatusReceiver = ProvidersStateReceiver()
            context.registerReceiver(providersStatusReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        }

        if (isGpsEnabled()) {
            setupGeofencing(locationBounds)
        } else {
            onStatusUpdate(GeofencingStatus.GPS_UNAVAILABLE)
        }
    }

    fun updateBounds(locationBounds: LocationBounds) {
        setupGeofencing(locationBounds)
    }

    @SuppressLint("MissingPermission")
    private fun setupGeofencing(locationBounds: LocationBounds) {
        when {
            locationBounds.isEmpty() -> {
                logw { "Location bounds is not set" }
                return
            }
            locationBounds.radius == 0f -> {
                logd { "Geofences radius is not set" }
                return
            }
        }
        val geofence = makeGeofence(locationBounds)

        geofencingClient?.addGeofences(getGeofencingRequest(geofence), geofencePendingIntent)?.run {
            addOnSuccessListener {
                logd { "Geofencing setup success" }
                isGeofencingSet = true
            }

            addOnFailureListener {
                loge(it) { "Geofencing setup error" }
                onStatusUpdate(GeofencingStatus.ERROR)
                isGeofencingSet = false
            }

            addOnCanceledListener {
                logd { "Geofencing cancelled" }
                isGeofencingSet = false
            }
        }
    }

    private fun setupGeofencingDelayed(locationBounds: LocationBounds) {
        delayedGeofenceSetupHandler.removeCallbacksAndMessages(null)
        delayedGeofenceSetupHandler.postDelayed({
            setupGeofencing(locationBounds)
        }, SETUP_DELAY)
    }

    private fun makeGeofence(locationBounds: LocationBounds): Geofence = Geofence.Builder()
            .setRequestId(GEOCFENCE_ID)
            .setCircularRegion(
                    locationBounds.center.latitude,
                    locationBounds.center.longitude,
                    locationBounds.radius
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofencesTrackerIntentService::class.java)
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER
                    or GeofencingRequest.INITIAL_TRIGGER_EXIT
                    or GeofencingRequest.INITIAL_TRIGGER_DWELL)
                    .addGeofence(geofence)
        }.build()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGeofencingStatusUpdate(event: GeofencingStatusUpdate) {
        when {
            event.geofencingStatus == GeofencingStatus.ERROR && isGeofencingSet -> {
                isGeofencingSet = false
                geofencingClient?.removeGeofences(geofencePendingIntent)
                onStatusUpdate(GeofencingStatus.GPS_UNAVAILABLE)
            }
            else -> onStatusUpdate(event.geofencingStatus)
        }
    }

    private fun onProviderStatusUpdate() {
        val isGpsEnabled = isGpsEnabled()
        logi { "Providers updated, GPS enabled: $isGpsEnabled" }
        if (isGpsEnabled && !isGeofencingSet) {
            lastLocationBounds?.let { setupGeofencingDelayed(it) }
        }
    }

    private fun isGpsEnabled(): Boolean = locationManager
            ?.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ?: false

    fun unregister() {
        logv { "Unregistering tracker" }
        delayedGeofenceSetupHandler.removeCallbacksAndMessages(null)
        geofencingClient?.removeGeofences(geofencePendingIntent)
        bus.unregister(this)
        providersStatusReceiver?.let {
            context.unregisterReceiver(providersStatusReceiver)
        }
        isGeofencingSet = false
    }

    companion object {
        private const val GEOCFENCE_ID = "primaryGeofence"
        private const val SETUP_DELAY = 5000L
    }

    inner class ProvidersStateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onProviderStatusUpdate()
        }
    }
}