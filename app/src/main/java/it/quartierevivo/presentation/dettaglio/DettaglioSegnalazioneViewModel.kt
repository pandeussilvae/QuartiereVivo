package it.quartierevivo.presentation.dettaglio

import androidx.lifecycle.ViewModel
import it.quartierevivo.domain.model.RuoloUtente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DettaglioSegnalazioneViewModel : ViewModel() {
    private val _ruoloUtenteCorrente = MutableStateFlow(RuoloUtente.CITTADINO)
    val ruoloUtenteCorrente: StateFlow<RuoloUtente> = _ruoloUtenteCorrente.asStateFlow()

    fun setRuoloUtente(ruoloUtente: RuoloUtente) {
        _ruoloUtenteCorrente.value = ruoloUtente
    }
}
