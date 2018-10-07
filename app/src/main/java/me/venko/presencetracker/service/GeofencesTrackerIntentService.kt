package me.venko.presencetracker.service

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import me.venko.presencetracker.data.events.GeofencingStatusUpdate
import me.venko.presencetracker.data.tracker.GeofencingStatus
import me.venko.presencetracker.utils.logd
import me.venko.presencetracker.utils.loge
import me.venko.presencetracker.utils.logw
import org.greenrobot.eventbus.EventBus

/**
 * @author Victor Kosenko
 *
 */
class GeofencesTrackerIntentService : IntentService("Geofences Service") {

    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            loge { "Geocences handling error: ${geofencingEvent.errorCode}" }
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val bus = EventBus.getDefault()
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                logd { "Geofences enter" }
                bus.post(GeofencingStatusUpdate(GeofencingStatus.INSIDE))
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                logd { "Geofences exit" }
                bus.post(GeofencingStatusUpdate(GeofencingStatus.OUTSIDE))
            }
            else -> logw { "Unexpected geofences status: $geofenceTransition" }
        }
    }

}