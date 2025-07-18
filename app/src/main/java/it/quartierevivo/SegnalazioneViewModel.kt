package it.quartierevivo

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SegnalazioneViewModel : ViewModel() {
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
    var invioConfermato by mutableStateOf(false)
        private set

    fun onTitoloChange(value: String) { titolo = value }
    fun onDescrizioneChange(value: String) { descrizione = value }
    fun onCategoriaChange(value: String) { categoria = value }
    fun onFotoChange(uri: Uri?) { fotoUri = uri }
    fun onPosizioneChange(value: String?) { posizione = value }

    fun inviaSegnalazione() {
        // Placeholder for actual send logic (e.g., Firebase)
        invioConfermato = true
    }

    fun resetConferma() {
        invioConfermato = false
    }
}
