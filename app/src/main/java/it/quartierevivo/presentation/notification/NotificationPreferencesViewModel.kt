package it.quartierevivo.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import it.quartierevivo.data.repository.NotificationPreferences
import it.quartierevivo.data.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationPreferencesViewModel(
    private val repository: NotificationPreferencesRepository,
    private val firebaseMessaging: FirebaseMessaging = FirebaseMessaging.getInstance(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationPreferences())
    val uiState: StateFlow<NotificationPreferences> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.preferences.collect { preferences ->
                _uiState.value = preferences
            }
        }
    }

    fun setCategoryEnabled(category: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.setCategoryEnabled(category, enabled)
            updateTopicSubscription(topic = "category_${category.sanitizeTopicPart()}", enabled = enabled)
        }
    }

    fun setZoneEnabled(zone: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.setZoneEnabled(zone, enabled)
            updateTopicSubscription(topic = "zone_${zone.sanitizeTopicPart()}", enabled = enabled)
        }
    }

    private fun updateTopicSubscription(topic: String, enabled: Boolean) {
        if (enabled) {
            firebaseMessaging.subscribeToTopic(topic)
        } else {
            firebaseMessaging.unsubscribeFromTopic(topic)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class NotificationPreferencesViewModelFactory(
    private val repository: NotificationPreferencesRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationPreferencesViewModel(repository) as T
    }
}

private fun String.sanitizeTopicPart(): String {
    return lowercase().replace(" ", "_").replace(Regex("[^a-z0-9_-]"), "")
}
