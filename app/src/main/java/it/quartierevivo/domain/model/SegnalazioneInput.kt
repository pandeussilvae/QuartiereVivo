package it.quartierevivo.domain.model

import android.net.Uri

data class SegnalazioneInput(
    val titolo: String,
    val descrizione: String,
    val categoria: String,
    val fotoUri: Uri?,
    val latitudine: Double?,
    val longitudine: Double?,
)
