package it.quartierevivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.quartierevivo.domain.usecase.FilterSegnalazioniByCategoriaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MappaSegnalazioniViewModel(
    private val filterSegnalazioniByCategoriaUseCase: FilterSegnalazioniByCategoriaUseCase =
        FilterSegnalazioniByCategoriaUseCase(),
) : ViewModel() {
    private val _segnalazioni = MutableStateFlow<List<Segnalazione>>(emptyList())
    val segnalazioni: StateFlow<List<Segnalazione>> = _segnalazioni

    private val _categoriaFiltro = MutableStateFlow<String?>(null)
    val categoriaFiltro: StateFlow<String?> = _categoriaFiltro

    val segnalazioniFiltrate: StateFlow<List<Segnalazione>> =
        combine(_segnalazioni, _categoriaFiltro) { lista, categoria ->
            filterSegnalazioniByCategoriaUseCase(lista, categoria)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun aggiornaSegnalazioni(nuoveSegnalazioni: List<Segnalazione>) {
        _segnalazioni.value = nuoveSegnalazioni
    }

    fun setCategoriaFiltro(categoria: String?) {
        _categoriaFiltro.value = categoria
    }
}
