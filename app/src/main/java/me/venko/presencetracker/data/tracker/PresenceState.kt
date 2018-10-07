package me.venko.presencetracker.data.tracker

/**
 * @author Victor Kosenko
 *
 */
enum class PresenceState {
    EVALUATING, INSIDE_GEO, INSIDE_WIFI, OUTSIDE_BOTH, OUTSIDE, NO_PERMISSIONS, UNKNOWN
}