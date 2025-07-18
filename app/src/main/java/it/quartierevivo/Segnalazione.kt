package it.quartierevivo

import android.net.Uri

data class Segnalazione(
    val titolo: String,
    val descrizione: String,
    val categoria: String,
    val fotoUri: Uri?,
    val posizione: String?
)
