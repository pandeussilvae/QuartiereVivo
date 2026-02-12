package it.quartierevivo.presentation.segnalazione

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.quartierevivo.domain.model.SegnalazioneInput
import it.quartierevivo.domain.usecase.InviaSegnalazioneUseCase
import it.quartierevivo.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SegnalazioneViewModel(
    private val inviaSegnalazioneUseCase: InviaSegnalazioneUseCase,
) : ViewModel() {
    var titolo by mutableStateOf("")
        private set
    var descrizione by mutableStateOf("")
        private set
    var categoria by mutableStateOf("")
        private set
    var fotoUri by mutableStateOf<Uri?>(null)
        private set
    var posizione by mutableStateOf<String?>(null)
        private set

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun onTitoloChange(value: String) { titolo = value }
    fun onDescrizioneChange(value: String) { descrizione = value }
    fun onCategoriaChange(value: String) { categoria = value }
    fun onFotoChange(uri: Uri?) { fotoUri = uri }
    fun onPosizioneChange(value: String?) { posizione = value }

    fun inviaSegnalazione() {
        viewModelScope.launch {
            if (titolo.isBlank() || categoria.isBlank()) {
                _uiState.value = UiState.Error("Titolo e categoria sono obbligatori")
                return@launch
            }

            _uiState.value = UiState.Loading
            val result = inviaSegnalazioneUseCase(
                SegnalazioneInput(
                    titolo = titolo,
                    descrizione = descrizione,
                    categoria = categoria,
                    fotoUri = fotoUri,
                    latitudine = posizione?.substringAfter("Lat:")?.substringBefore(",")?.trim()?.toDoubleOrNull(),
                    longitudine = posizione?.substringAfter("Lng:")?.trim()?.toDoubleOrNull(),
                )
            )
            _uiState.value = if (result.isSuccess) {
                clearForm()
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.localizedMessage ?: "Errore durante l'invio")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Empty
    }

    private fun clearForm() {
        titolo = ""
        descrizione = ""
        categoria = ""
        fotoUri = null
        posizione = null
    }
}
