package com.jairo.calendariotrabajo.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(private val context: Context) {

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_NOTIFICATIONS_ENABLED] ?: false
    }

    val reminderHour: Flow<Int> = context.dataStore.data.map {
        it[KEY_REMINDER_HOUR] ?: DEFAULT_HOUR
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setReminderHour(hour: Int) {
        context.dataStore.edit { it[KEY_REMINDER_HOUR] = hour }
    }

    companion object {
        private const val DEFAULT_HOUR = 20

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_REMINDER_HOUR = intPreferencesKey("reminder_hour")
    }
}
