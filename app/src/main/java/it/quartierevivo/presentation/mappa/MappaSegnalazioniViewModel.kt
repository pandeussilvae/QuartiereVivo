package it.quartierevivo.presentation.mappa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.quartierevivo.domain.model.Segnalazione
import it.quartierevivo.domain.usecase.ObserveSegnalazioniUseCase
import it.quartierevivo.domain.usecase.SeedSegnalazioniUseCase
import it.quartierevivo.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MappaSegnalazioniViewModel(
    observeSegnalazioniUseCase: ObserveSegnalazioniUseCase,
    seedSegnalazioniUseCase: SeedSegnalazioniUseCase,
) : ViewModel() {

    private val categoriaFiltro = MutableStateFlow<String?>(null)

    private val allSegnalazioniUiState: StateFlow<UiState<List<Segnalazione>>> =
        observeSegnalazioniUseCase()
            .map<List<Segnalazione>, UiState<List<Segnalazione>>> { segnalazioni ->
                if (segnalazioni.isEmpty()) UiState.Empty else UiState.Success(segnalazioni)
            }
            .catch { throwable ->
                emit(UiState.Error(throwable.localizedMessage ?: "Errore caricamento segnalazioni"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val uiState: StateFlow<UiState<List<Segnalazione>>> =
        combine(allSegnalazioniUiState, categoriaFiltro) { currentState, categoria ->
            when (currentState) {
                is UiState.Success -> {
                    val filtrate = if (categoria.isNullOrBlank()) {
                        currentState.data
                    } else {
                        currentState.data.filter { it.categoria == categoria }
                    }
                    if (filtrate.isEmpty()) UiState.Empty else UiState.Success(filtrate)
                }
                UiState.Empty -> UiState.Empty
                is UiState.Error -> currentState
                UiState.Loading -> UiState.Loading
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    init {
        viewModelScope.launch {
            runCatching { seedSegnalazioniUseCase() }
        }
    }

    fun setCategoriaFiltro(categoria: String?) {
        categoriaFiltro.value = categoria
    }

    fun getCategoriaFiltro(): StateFlow<String?> = categoriaFiltro
}
