package it.quartierevivo.data.repository

import com.google.firebase.firestore.FieldValue
import it.quartierevivo.data.remote.FirebaseAuthService
import it.quartierevivo.data.remote.FirestoreService
import it.quartierevivo.data.remote.StorageService
import it.quartierevivo.domain.model.Segnalazione
import it.quartierevivo.domain.model.SegnalazioneInput
import it.quartierevivo.domain.model.SegnalazioneSchema
import it.quartierevivo.domain.model.StatoSegnalazione
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
            val creatorId = authService.currentUserId().orEmpty()
            val imageUrl = input.fotoUri?.let { uri ->
                storageService.uploadSegnalazioneImage(documentId, uri)
            }

            val payload = mapOf(
                SegnalazioneSchema.TITOLO to input.titolo,
                SegnalazioneSchema.DESCRIZIONE to input.descrizione,
                SegnalazioneSchema.CATEGORIA to input.categoria,
                SegnalazioneSchema.LATITUDINE to (input.latitudine ?: 0.0),
                SegnalazioneSchema.LONGITUDINE to (input.longitudine ?: 0.0),
                SegnalazioneSchema.IMMAGINE_URL to imageUrl,
                SegnalazioneSchema.CREATORE_ID to creatorId,
                SegnalazioneSchema.STATO to StatoSegnalazione.NUOVA.wireValue,
                SegnalazioneSchema.CREATED_AT to FieldValue.serverTimestamp(),
                SegnalazioneSchema.UPDATED_AT to FieldValue.serverTimestamp(),
                SegnalazioneSchema.UPDATED_BY to creatorId,
            )

            firestoreService.saveSegnalazione(documentId, payload)
        }
    }

    override suspend fun seedSegnalazioniIfEmpty() {
        if (!firestoreService.isSegnalazioniEmpty()) return

        val samples = listOf(
            mapOf(
                SegnalazioneSchema.TITOLO to "Guasto illuminazione",
                SegnalazioneSchema.DESCRIZIONE to "Lampione non funzionante in piazza principale",
                SegnalazioneSchema.CATEGORIA to "Manutenzione",
                SegnalazioneSchema.LATITUDINE to 45.4641,
                SegnalazioneSchema.LONGITUDINE to 9.1916,
                SegnalazioneSchema.IMMAGINE_URL to null,
                SegnalazioneSchema.CREATORE_ID to "seed",
                SegnalazioneSchema.STATO to StatoSegnalazione.NUOVA.wireValue,
                SegnalazioneSchema.CREATED_AT to FieldValue.serverTimestamp(),
                SegnalazioneSchema.UPDATED_AT to FieldValue.serverTimestamp(),
                SegnalazioneSchema.UPDATED_BY to "seed",
            ),
            mapOf(
                SegnalazioneSchema.TITOLO to "Rifiuti abbandonati",
                SegnalazioneSchema.DESCRIZIONE to "Sacchi lasciati vicino al parco",
                SegnalazioneSchema.CATEGORIA to "Altro",
                SegnalazioneSchema.LATITUDINE to 45.4650,
                SegnalazioneSchema.LONGITUDINE to 9.1890,
                SegnalazioneSchema.IMMAGINE_URL to null,
                SegnalazioneSchema.CREATORE_ID to "seed",
                SegnalazioneSchema.STATO to StatoSegnalazione.NUOVA.wireValue,
                SegnalazioneSchema.CREATED_AT to FieldValue.serverTimestamp(),
                SegnalazioneSchema.UPDATED_AT to FieldValue.serverTimestamp(),
                SegnalazioneSchema.UPDATED_BY to "seed",
            ),
            mapOf(
                SegnalazioneSchema.TITOLO to "Area pericolosa",
                SegnalazioneSchema.DESCRIZIONE to "Buche profonde sul marciapiede",
                SegnalazioneSchema.CATEGORIA to "Sicurezza",
                SegnalazioneSchema.LATITUDINE to 45.4630,
                SegnalazioneSchema.LONGITUDINE to 9.1880,
                SegnalazioneSchema.IMMAGINE_URL to null,
                SegnalazioneSchema.CREATORE_ID to "seed",
                SegnalazioneSchema.STATO to StatoSegnalazione.NUOVA.wireValue,
                SegnalazioneSchema.CREATED_AT to FieldValue.serverTimestamp(),
                SegnalazioneSchema.UPDATED_AT to FieldValue.serverTimestamp(),
                SegnalazioneSchema.UPDATED_BY to "seed",
            ),
        )

        samples.forEach { sample ->
            val id = firestoreService.createSegnalazioneId()
            firestoreService.saveSegnalazione(id, sample)
        }
    }
}
