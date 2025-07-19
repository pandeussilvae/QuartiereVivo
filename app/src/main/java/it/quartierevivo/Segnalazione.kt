package it.quartierevivo

import android.net.Uri

data class Segnalazione(
    val titolo: String,
    val descrizione: String,
    val categoria: String,
    val fotoUri: Uri?,
    val latitudine: Double,
    val longitudine: Double,
    val risolta: Boolean = false,
    val posizione: String? = null
)
