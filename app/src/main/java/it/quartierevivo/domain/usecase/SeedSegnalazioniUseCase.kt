package it.quartierevivo.domain.usecase

import it.quartierevivo.domain.repository.SegnalazioneRepository

class SeedSegnalazioniUseCase(
    private val repository: SegnalazioneRepository,
) {
    suspend operator fun invoke() = repository.seedSegnalazioniIfEmpty()
}
