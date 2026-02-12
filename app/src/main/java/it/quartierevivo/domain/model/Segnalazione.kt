package it.quartierevivo.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Segnalazione(
    val id: String,
    val titolo: String,
    val descrizione: String = "",
    val latitudine: Double,
    val longitudine: Double,
    val immagineUrl: String? = null,
    val categoria: String = "",
    val creatoreId: String? = null,
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(latitudine, longitudine)
    override fun getTitle(): String = titolo
    override fun getSnippet(): String = descrizione
    override fun getZIndex(): Float? = null
}
