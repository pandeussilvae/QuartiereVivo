package it.quartierevivo.domain.repository

import it.quartierevivo.domain.model.Segnalazione
import it.quartierevivo.domain.model.SegnalazioneInput
import kotlinx.coroutines.flow.Flow

interface SegnalazioneRepository {
    fun observeSegnalazioni(): Flow<List<Segnalazione>>
    suspend fun inviaSegnalazione(input: SegnalazioneInput): Result<Unit>
    suspend fun seedSegnalazioniIfEmpty()
}
