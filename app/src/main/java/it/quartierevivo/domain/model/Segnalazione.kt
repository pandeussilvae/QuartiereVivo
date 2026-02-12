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
    val stato: StatoSegnalazione = StatoSegnalazione.NUOVA,
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(latitudine, longitudine)
    override fun getTitle(): String = titolo
    override fun getSnippet(): String = descrizione
    override fun getZIndex(): Float? = null
}

data class SegnalazioneCampoSchema(
    val nome: String,
    val tipo: String,
    val obbligatorio: Boolean,
)

object SegnalazioneSchema {
    const val COLLECTION = "segnalazioni"

    const val TITOLO = "titolo"
    const val DESCRIZIONE = "descrizione"
    const val CATEGORIA = "categoria"
    const val LATITUDINE = "latitudine"
    const val LONGITUDINE = "longitudine"
    const val IMMAGINE_URL = "immagineUrl"
    const val CREATORE_ID = "creatoreId"
    const val STATO = "stato"
    const val CREATED_AT = "createdAt"
    const val UPDATED_AT = "updatedAt"
    const val UPDATED_BY = "updatedBy"

    val campi = listOf(
        SegnalazioneCampoSchema(TITOLO, "string", true),
        SegnalazioneCampoSchema(DESCRIZIONE, "string", true),
        SegnalazioneCampoSchema(CATEGORIA, "string", true),
        SegnalazioneCampoSchema(LATITUDINE, "number", true),
        SegnalazioneCampoSchema(LONGITUDINE, "number", true),
        SegnalazioneCampoSchema(IMMAGINE_URL, "string|null", false),
        SegnalazioneCampoSchema(CREATORE_ID, "string", true),
        SegnalazioneCampoSchema(STATO, "string(enum)", true),
        SegnalazioneCampoSchema(CREATED_AT, "timestamp", true),
        SegnalazioneCampoSchema(UPDATED_AT, "timestamp", true),
        SegnalazioneCampoSchema(UPDATED_BY, "string", true),
    )
}
