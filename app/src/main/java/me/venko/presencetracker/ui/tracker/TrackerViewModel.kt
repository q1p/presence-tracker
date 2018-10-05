package me.venko.presencetracker.ui.tracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import me.venko.presencetracker.data.settings.SettingsRepository

/**
 * @author Victor Kosenko
 */
class TrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val settings by lazy { SettingsRepository.instance }
}
