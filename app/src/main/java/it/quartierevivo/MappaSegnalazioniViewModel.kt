package it.quartierevivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class MappaSegnalazioniViewModel : ViewModel() {
    private val _segnalazioni = MutableStateFlow<List<Segnalazione>>(emptyList())
    val segnalazioni: StateFlow<List<Segnalazione>> = _segnalazioni

    private val _categoriaFiltro = MutableStateFlow<String?>(null)
    val categoriaFiltro: StateFlow<String?> = _categoriaFiltro

    val segnalazioniFiltrate: StateFlow<List<Segnalazione>> =
        combine(_segnalazioni, _categoriaFiltro) { lista, categoria ->
            if (categoria.isNullOrBlank()) lista
            else lista.filter { it.categoria == categoria }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun aggiornaSegnalazioni(nuoveSegnalazioni: List<Segnalazione>) {
        _segnalazioni.value = nuoveSegnalazioni
    }

    fun setCategoriaFiltro(categoria: String?) {
        _categoriaFiltro.value = categoria
    }

    fun getSegnalazioneById(id: String): Segnalazione? =
        _segnalazioni.value.firstOrNull { it.id == id }

    fun aggiornaStatusSegnalazione(
        id: String,
        nuovoStatus: StatoSegnalazione,
        ruoloUtente: RuoloUtente,
        autoreAggiornamento: String,
        nota: String = ""
    ): Boolean {
        if (ruoloUtente == RuoloUtente.CITTADINO) {
            return false
        }

        _segnalazioni.value = _segnalazioni.value.map { segnalazione ->
            if (segnalazione.id != id) {
                segnalazione
            } else {
                segnalazione.copy(
                    status = nuovoStatus,
                    storicoAggiornamenti = segnalazione.storicoAggiornamenti + AggiornamentoStato(
                        status = nuovoStatus,
                        autore = autoreAggiornamento,
                        dataAggiornamento = LocalDateTime.now(),
                        nota = nota
                    )
                )
            }
        }
        return true
    }
}
