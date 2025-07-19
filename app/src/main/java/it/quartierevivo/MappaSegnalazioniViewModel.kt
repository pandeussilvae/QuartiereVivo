package it.quartierevivo

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

/**
 * ViewModel per la schermata con la mappa delle segnalazioni.
 */
class MappaSegnalazioniViewModel : ViewModel() {

    private val _segnalazioni = mutableStateListOf<Segnalazione>()
    val segnalazioni: List<Segnalazione> get() = _segnalazioni

    private val _segnalazioneSelezionata = mutableStateOf<Segnalazione?>(null)
    val segnalazioneSelezionata: State<Segnalazione?> get() = _segnalazioneSelezionata

    init {
        // Dati di esempio
        _segnalazioni += Segnalazione(
            titolo = "Panchina rotta",
            descrizione = "La panchina vicino al parco \u00e8 danneggiata.",
            categoria = "Manutenzione",
            fotoUri = null,
            posizione = LatLng(41.0, 12.0),
            dataInvio = "01/01/2024"
        )
    }

    fun onMarkerSelected(segnalazione: Segnalazione) {
        _segnalazioneSelezionata.value = segnalazione
    }

    fun chiudiDettaglio() {
        _segnalazioneSelezionata.value = null
    }

    fun segnalaRisolta() {
        // In realt\u00e0 dovremmo aggiornare il backend
        _segnalazioneSelezionata.value = null
    }
}
