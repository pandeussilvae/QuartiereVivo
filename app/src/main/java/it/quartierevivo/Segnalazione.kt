package it.quartierevivo

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Dati di una segnalazione geolocalizzata.
 */
data class Segnalazione(
    val id: String,
    val titolo: String,
    val descrizione: String = "",
    val latitudine: Double,
    val longitudine: Double,
    val immagineUrl: String? = null,
    val categoria: String = "",
    val zona: String = "",
    val status: String = "Aperta",
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(latitudine, longitudine)
    override fun getTitle(): String = titolo
    override fun getSnippet(): String? = null
    override fun getZIndex(): Float? = null
}
