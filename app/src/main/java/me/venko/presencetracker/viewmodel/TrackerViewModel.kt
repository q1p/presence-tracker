package me.venko.presencetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import me.venko.presencetracker.data.settings.SettingsRepository
import me.venko.presencetracker.utils.logd

/**
 * @author Victor Kosenko
 */
class TrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val settings by lazy { SettingsRepository.instance }
}
