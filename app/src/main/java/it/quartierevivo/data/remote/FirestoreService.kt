package it.quartierevivo.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import it.quartierevivo.domain.model.Segnalazione
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val segnalazioniCollection = firestore.collection("segnalazioni")

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
                val lat = doc.getDouble("latitudine")
                val lng = doc.getDouble("longitudine")
                if (lat == null || lng == null) {
                    null
                } else {
                    Segnalazione(
                        id = doc.id,
                        titolo = doc.getString("titolo").orEmpty(),
                        descrizione = doc.getString("descrizione").orEmpty(),
                        latitudine = lat,
                        longitudine = lng,
                        immagineUrl = doc.getString("immagineUrl"),
                        categoria = doc.getString("categoria").orEmpty(),
                        creatoreId = doc.getString("creatoreId"),
                    )
                }
            }
            trySend(items)
        }
        awaitClose { listener.remove() }
    }
}
