package com.jairo.calendariotrabajo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jairo.calendariotrabajo.data.preferences.AppPreferences
import com.jairo.calendariotrabajo.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appPreferences: AppPreferences,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val notificationsEnabled: StateFlow<Boolean> = appPreferences.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        appPreferences.setNotificationsEnabled(enabled)
        if (enabled) {
            val hour = appPreferences.reminderHour.first()
            reminderScheduler.schedule(hour = hour)
        } else {
            reminderScheduler.cancel()
        }
    }

    companion object {
        fun factory(
            appPreferences: AppPreferences,
            reminderScheduler: ReminderScheduler
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(appPreferences, reminderScheduler)
            }
        }
    }
}
