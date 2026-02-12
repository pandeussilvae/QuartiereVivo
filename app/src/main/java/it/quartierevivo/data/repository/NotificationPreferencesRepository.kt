package it.quartierevivo.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore by preferencesDataStore(name = "notification_preferences")

data class NotificationPreferences(
    val categorie: Set<String> = emptySet(),
    val zone: Set<String> = emptySet(),
)

class NotificationPreferencesRepository(private val context: Context) {
    private companion object {
        val CATEGORIES_KEY = stringSetPreferencesKey("notification_categories")
        val ZONES_KEY = stringSetPreferencesKey("notification_zones")
    }

    val preferences: Flow<NotificationPreferences> = context.notificationDataStore.data.map { values ->
        NotificationPreferences(
            categorie = values[CATEGORIES_KEY] ?: emptySet(),
            zone = values[ZONES_KEY] ?: emptySet(),
        )
    }

    suspend fun setCategoryEnabled(category: String, enabled: Boolean) {
        context.notificationDataStore.edit { values ->
            val updated = values[CATEGORIES_KEY]?.toMutableSet() ?: mutableSetOf()
            if (enabled) updated.add(category) else updated.remove(category)
            values[CATEGORIES_KEY] = updated
        }
    }

    suspend fun setZoneEnabled(zone: String, enabled: Boolean) {
        context.notificationDataStore.edit { values ->
            val updated = values[ZONES_KEY]?.toMutableSet() ?: mutableSetOf()
            if (enabled) updated.add(zone) else updated.remove(zone)
            values[ZONES_KEY] = updated
        }
    }
}
