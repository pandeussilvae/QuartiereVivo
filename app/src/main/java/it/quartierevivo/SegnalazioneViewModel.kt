package it.quartierevivo

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

private const val PRIVACY_CONSENT_TEXT =
    "Acconsento all'uso della mia posizione solo per associare la segnalazione al punto corretto sulla mappa del quartiere."

enum class PermissionState {
    Unknown,
    Granted,
    Denied,
    PermanentlyDenied,
}

data class SegnalazioneUiState(
    val titolo: String = "",
    val descrizione: String = "",
    val categoria: String = "",
    val fotoUri: Uri? = null,
    val posizione: String? = null,
    val invioConfermato: Boolean = false,
    val cameraPermissionState: PermissionState = PermissionState.Unknown,
    val locationPermissionState: PermissionState = PermissionState.Unknown,
    val privacyConsentGiven: Boolean = false,
    val privacyConsentText: String = PRIVACY_CONSENT_TEXT,
)

class SegnalazioneViewModel : ViewModel() {
    var uiState by mutableStateOf(SegnalazioneUiState())
        private set

    fun onTitoloChange(value: String) {
        uiState = uiState.copy(titolo = value)
    }

    fun onDescrizioneChange(value: String) {
        uiState = uiState.copy(descrizione = value)
    }

    fun onCategoriaChange(value: String) {
        uiState = uiState.copy(categoria = value)
    }

    fun onFotoChange(uri: Uri?) {
        uiState = uiState.copy(fotoUri = uri)
    }

    fun onPosizioneChange(value: String?) {
        uiState = uiState.copy(posizione = value)
    }

    fun onPrivacyConsentChange(consentGiven: Boolean) {
        uiState = uiState.copy(privacyConsentGiven = consentGiven)
    }

    fun onCameraPermissionStateChange(permissionState: PermissionState) {
        uiState = uiState.copy(cameraPermissionState = permissionState)
    }

    fun onLocationPermissionStateChange(permissionState: PermissionState) {
        uiState = uiState.copy(locationPermissionState = permissionState)
    }

    fun inviaSegnalazione() {
        uiState = uiState.copy(invioConfermato = true)
    }

    fun resetConferma() {
        uiState = uiState.copy(invioConfermato = false)
    }
}
