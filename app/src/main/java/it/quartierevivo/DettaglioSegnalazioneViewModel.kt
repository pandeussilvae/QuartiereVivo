package it.quartierevivo

import androidx.lifecycle.ViewModel
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
