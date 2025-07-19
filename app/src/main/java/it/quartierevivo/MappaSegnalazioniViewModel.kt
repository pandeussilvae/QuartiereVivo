package it.quartierevivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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
}
