package it.quartierevivo.domain.usecase

import it.quartierevivo.domain.model.SegnalazioneInput
import it.quartierevivo.domain.repository.SegnalazioneRepository

class InviaSegnalazioneUseCase(
    private val repository: SegnalazioneRepository,
) {
    suspend operator fun invoke(input: SegnalazioneInput): Result<Unit> = repository.inviaSegnalazione(input)
}
