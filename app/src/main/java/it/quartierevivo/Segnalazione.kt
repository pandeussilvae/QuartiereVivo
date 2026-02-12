package it.quartierevivo

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.maps.android.clustering.ClusterItem

/**
 * Dati di una segnalazione geolocalizzata.
 */
data class Segnalazione(
    val id: String = "",
    val titolo: String = "",
    val descrizione: String = "",
    val categoria: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val imageUrl: String? = null,
    val status: String = STATUS_OPEN,
    val createdAt: Timestamp? = null,
    val createdBy: String = "",
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(lat, lng)
    override fun getTitle(): String = titolo
    override fun getSnippet(): String = descrizione
    override fun getZIndex(): Float? = null

    companion object {
        const val COLLECTION = "segnalazioni"
        const val STATUS_OPEN = "OPEN"

        fun fromDocument(document: DocumentSnapshot): Segnalazione? {
            val lat = document.getDouble("lat") ?: return null
            val lng = document.getDouble("lng") ?: return null

            return Segnalazione(
                id = document.id,
                titolo = document.getString("titolo").orEmpty(),
                descrizione = document.getString("descrizione").orEmpty(),
                categoria = document.getString("categoria").orEmpty(),
                lat = lat,
                lng = lng,
                imageUrl = document.getString("imageUrl"),
                status = document.getString("status") ?: STATUS_OPEN,
                createdAt = document.getTimestamp("createdAt"),
                createdBy = document.getString("createdBy").orEmpty(),
            )
        }
    }
}

fun Segnalazione.toFirestorePayload(): Map<String, Any?> = mapOf(
    "titolo" to titolo,
    "descrizione" to descrizione,
    "categoria" to categoria,
    "lat" to lat,
    "lng" to lng,
    "imageUrl" to imageUrl,
    "status" to status,
    "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
    "createdBy" to createdBy,
)
