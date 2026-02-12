package it.quartierevivo.data.repository

import it.quartierevivo.data.remote.FirebaseAuthService
import it.quartierevivo.data.remote.FirestoreService
import it.quartierevivo.data.remote.StorageService
import it.quartierevivo.domain.model.Segnalazione
import it.quartierevivo.domain.model.SegnalazioneInput
import it.quartierevivo.domain.repository.SegnalazioneRepository
import kotlinx.coroutines.flow.Flow

class FirebaseSegnalazioneRepository(
    private val firestoreService: FirestoreService,
    private val storageService: StorageService,
    private val authService: FirebaseAuthService,
) : SegnalazioneRepository {

    override fun observeSegnalazioni(): Flow<List<Segnalazione>> = firestoreService.observeSegnalazioni()

    override suspend fun inviaSegnalazione(input: SegnalazioneInput): Result<Unit> {
        return runCatching {
            val documentId = firestoreService.createSegnalazioneId()
            val imageUrl = input.fotoUri?.let { uri ->
                storageService.uploadSegnalazioneImage(documentId, uri)
            }

            val payload = mapOf(
                "titolo" to input.titolo,
                "descrizione" to input.descrizione,
                "categoria" to input.categoria,
                "latitudine" to (input.latitudine ?: 0.0),
                "longitudine" to (input.longitudine ?: 0.0),
                "immagineUrl" to imageUrl,
                "creatoreId" to authService.currentUserId(),
            )

            firestoreService.saveSegnalazione(documentId, payload)
        }
    }

    override suspend fun seedSegnalazioniIfEmpty() {
        if (!firestoreService.isSegnalazioniEmpty()) return

        val samples = listOf(
            mapOf(
                "titolo" to "Guasto illuminazione",
                "descrizione" to "Lampione non funzionante in piazza principale",
                "categoria" to "Manutenzione",
                "latitudine" to 45.4641,
                "longitudine" to 9.1916,
                "immagineUrl" to null,
                "creatoreId" to "seed",
            ),
            mapOf(
                "titolo" to "Rifiuti abbandonati",
                "descrizione" to "Sacchi lasciati vicino al parco",
                "categoria" to "Altro",
                "latitudine" to 45.4650,
                "longitudine" to 9.1890,
                "immagineUrl" to null,
                "creatoreId" to "seed",
            ),
            mapOf(
                "titolo" to "Area pericolosa",
                "descrizione" to "Buche profonde sul marciapiede",
                "categoria" to "Sicurezza",
                "latitudine" to 45.4630,
                "longitudine" to 9.1880,
                "immagineUrl" to null,
                "creatoreId" to "seed",
            ),
        )

        samples.forEach { sample ->
            val id = firestoreService.createSegnalazioneId()
            firestoreService.saveSegnalazione(id, sample)
        }
    }
}
