package me.venko.presencetracker

import android.app.Application

/**
 * @author Victor Kosenko
 *
 */
class TrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TrackerApplication
    }
}