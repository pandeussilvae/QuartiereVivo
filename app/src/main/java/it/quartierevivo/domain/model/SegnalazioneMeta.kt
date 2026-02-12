package it.quartierevivo.domain.model

import java.time.LocalDateTime

enum class StatoSegnalazione(val label: String) {
    NUOVA("Nuova"),
    IN_CARICO("In carico"),
    RISOLTA("Risolta"),
}

enum class RuoloUtente {
    CITTADINO,
    MODERATORE,
    AMMINISTRATORE,
}

data class AggiornamentoStato(
    val status: StatoSegnalazione,
    val autore: String,
    val dataAggiornamento: LocalDateTime,
    val nota: String = "",
)
