package it.quartierevivo

@Deprecated(
    message = "Usare it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel",
    replaceWith = ReplaceWith("it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel"),
)
typealias MappaSegnalazioniViewModel = it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.quartierevivo.domain.usecase.FilterSegnalazioniByCategoriaUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class MappaSegnalazioniViewModel(
    private val filterSegnalazioniByCategoriaUseCase: FilterSegnalazioniByCategoriaUseCase =
        FilterSegnalazioniByCategoriaUseCase(),
) : ViewModel() {
class MappaSegnalazioniViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _segnalazioni = MutableStateFlow<List<Segnalazione>>(emptyList())
    val segnalazioni: StateFlow<List<Segnalazione>> = _segnalazioni

    private val _categoriaFiltro = MutableStateFlow<String?>(null)
    val categoriaFiltro: StateFlow<String?> = _categoriaFiltro

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errore = MutableStateFlow<String?>(null)
    val errore: StateFlow<String?> = _errore

    private var listenerRegistration: ListenerRegistration? = null

    val segnalazioniFiltrate: StateFlow<List<Segnalazione>> =
        combine(_segnalazioni, _categoriaFiltro) { lista, categoria ->
            filterSegnalazioniByCategoriaUseCase(lista, categoria)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        startRealtimeUpdates()
    }

    fun setCategoriaFiltro(categoria: String?) {
        _categoriaFiltro.value = categoria
    }

    fun getSegnalazioneById(id: String): Segnalazione? {
        return _segnalazioni.value.firstOrNull { it.id == id }
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
    fun retry() {
        startRealtimeUpdates()
    }

    fun clearError() {
        _errore.value = null
    }

    private fun startRealtimeUpdates() {
        listenerRegistration?.remove()
        _isLoading.value = true
        _errore.value = null

        listenerRegistration = firestore.collection(Segnalazione.COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errore.value = error.message ?: "Errore durante il caricamento segnalazioni"
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                val loaded = snapshot?.documents
                    ?.mapNotNull { Segnalazione.fromDocument(it) }
                    .orEmpty()
                    .sortedByDescending { it.createdAt?.seconds ?: 0L }

                _segnalazioni.value = loaded
                _isLoading.value = false
            }
    }

    override fun onCleared() {
        listenerRegistration?.remove()
        super.onCleared()
    fun tracciaAperturaDettaglio(segnalazioneId: String) {
        Firebase.analytics.logEvent("open_report_detail") {
            param("report_id", segnalazioneId)
        }
    }
}
