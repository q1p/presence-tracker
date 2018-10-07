package me.venko.presencetracker.data.events

import me.venko.presencetracker.data.tracker.GeofencingStatus

/**
 * @author Victor Kosenko
 *
 */

data class GeofencingStatusUpdate(val geofencingStatus: GeofencingStatus)