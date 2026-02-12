package it.quartierevivo.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import it.quartierevivo.domain.model.Segnalazione
import it.quartierevivo.domain.model.SegnalazioneSchema
import it.quartierevivo.domain.model.StatoSegnalazione
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val segnalazioniCollection = firestore.collection(SegnalazioneSchema.COLLECTION)

    fun createSegnalazioneId(): String = segnalazioniCollection.document().id

    suspend fun saveSegnalazione(documentId: String, payload: Map<String, Any?>) {
        segnalazioniCollection.document(documentId).set(payload).await()
    }

    suspend fun isSegnalazioniEmpty(): Boolean {
        return segnalazioniCollection.limit(1).get().await().isEmpty
    }

    fun observeSegnalazioni(): Flow<List<Segnalazione>> = callbackFlow {
        val listener = segnalazioniCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val items = snapshot?.documents.orEmpty().mapNotNull { doc ->
                val lat = doc.getDouble(SegnalazioneSchema.LATITUDINE)
                val lng = doc.getDouble(SegnalazioneSchema.LONGITUDINE)
                if (lat == null || lng == null) {
                    null
                } else {
                    Segnalazione(
                        id = doc.id,
                        titolo = doc.getString(SegnalazioneSchema.TITOLO).orEmpty(),
                        descrizione = doc.getString(SegnalazioneSchema.DESCRIZIONE).orEmpty(),
                        latitudine = lat,
                        longitudine = lng,
                        immagineUrl = doc.getString(SegnalazioneSchema.IMMAGINE_URL),
                        categoria = doc.getString(SegnalazioneSchema.CATEGORIA).orEmpty(),
                        creatoreId = doc.getString(SegnalazioneSchema.CREATORE_ID),
                        stato = StatoSegnalazione.fromWireValue(doc.getString(SegnalazioneSchema.STATO)),
                    )
                }
            }
            trySend(items)
        }
        awaitClose { listener.remove() }
    }
}
