package it.quartierevivo.domain.usecase

import it.quartierevivo.domain.model.Segnalazione

class FilterSegnalazioniByCategoriaUseCase {
    operator fun invoke(
        segnalazioni: List<Segnalazione>,
        categoria: String?,
    ): List<Segnalazione> {
        if (categoria.isNullOrBlank()) {
            return segnalazioni
        }

        return segnalazioni.filter { it.categoria == categoria }
    }
}
