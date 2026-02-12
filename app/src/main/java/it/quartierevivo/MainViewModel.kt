package it.quartierevivo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _pendingSegnalazioneDeepLink = MutableStateFlow<String?>(null)
    val pendingSegnalazioneDeepLink: StateFlow<String?> = _pendingSegnalazioneDeepLink.asStateFlow()

    fun onDeepLinkReceived(segnalazioneId: String?) {
        if (!segnalazioneId.isNullOrBlank()) {
            _pendingSegnalazioneDeepLink.value = segnalazioneId
        }
    }

    fun consumeDeepLink() {
        _pendingSegnalazioneDeepLink.value = null
    }
}
