package it.quartierevivo

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

/**
 * Rappresenta una segnalazione inserita dagli utenti.
 */
data class Segnalazione(
    val titolo: String,
    val descrizione: String,
    val categoria: String,
    val fotoUri: Uri?,
    val posizione: LatLng?,
    val dataInvio: String
)
