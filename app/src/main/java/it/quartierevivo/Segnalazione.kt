package it.quartierevivo

import java.time.LocalDateTime

@Deprecated(
    message = "Usare it.quartierevivo.domain.model.Segnalazione",
    replaceWith = ReplaceWith("it.quartierevivo.domain.model.Segnalazione"),
)
typealias Segnalazione = it.quartierevivo.domain.model.Segnalazione

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
