package it.quartierevivo.domain.usecase

import it.quartierevivo.domain.repository.SegnalazioneRepository

class ObserveSegnalazioniUseCase(
    private val repository: SegnalazioneRepository,
) {
    operator fun invoke() = repository.observeSegnalazioni()
}
