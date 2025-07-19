package it.quartierevivo

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
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
    var posizione by mutableStateOf<LatLng?>(null)
        private set
    var dataInvio by mutableStateOf("")
        private set
    var invioConfermato by mutableStateOf(false)
        private set

    fun onTitoloChange(value: String) { titolo = value }
    fun onDescrizioneChange(value: String) { descrizione = value }
    fun onCategoriaChange(value: String) { categoria = value }
    fun onFotoChange(uri: Uri?) { fotoUri = uri }
    fun onPosizioneChange(value: LatLng?) { posizione = value }
    fun onDataInvioChange(value: String) { dataInvio = value }

    fun inviaSegnalazione() {
        // Placeholder for actual send logic (e.g., Firebase)
        dataInvio = java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())
        invioConfermato = true
    }

    fun resetConferma() {
        invioConfermato = false
    }
}
