package it.quartierevivo.domain.model

import java.time.LocalDateTime

enum class StatoSegnalazione(val wireValue: String, val label: String) {
    NUOVA("nuova", "Nuova"),
    IN_CARICO("in_carico", "In carico"),
    RISOLTA("risolta", "Risolta"),
    ;

    companion object {
        val allowedWireValues: Set<String> = entries.map { it.wireValue }.toSet()

        fun fromWireValue(value: String?): StatoSegnalazione {
            return entries.firstOrNull { it.wireValue == value } ?: NUOVA
        }
    }
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
