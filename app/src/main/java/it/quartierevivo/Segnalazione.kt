package it.quartierevivo

/**
 * Dati di una segnalazione geolocalizzata.
 */
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.time.LocalDateTime

data class Segnalazione(
    val id: String,
    val titolo: String,
    val latitudine: Double,
    val longitudine: Double,
    val descrizione: String = "",
    val immagineUrl: String? = null,
    val categoria: String = "",
    val autore: String = "",
    val dataCreazione: LocalDateTime = LocalDateTime.now(),
    val status: StatoSegnalazione = StatoSegnalazione.NUOVA,
    val storicoAggiornamenti: List<AggiornamentoStato> = emptyList(),
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(latitudine, longitudine)
    override fun getTitle(): String = titolo
    override fun getSnippet(): String? = null
    override fun getZIndex(): Float? = null
}

enum class StatoSegnalazione(val label: String) {
    NUOVA("Nuova"),
    IN_CARICO("In carico"),
    RISOLTA("Risolta")
}

enum class RuoloUtente {
    CITTADINO,
    MODERATORE,
    AMMINISTRATORE
}

data class AggiornamentoStato(
    val status: StatoSegnalazione,
    val autore: String,
    val dataAggiornamento: LocalDateTime,
    val nota: String = ""
)
