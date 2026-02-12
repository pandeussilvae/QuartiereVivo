package it.quartierevivo

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs
import kotlin.math.roundToInt

class SegnalazioneViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val maxTitoloLength = 80
    private val maxDescrizioneLength = 500
    private val duplicateWindowHours = 24L

    var titolo by mutableStateOf("")
        private set
    var descrizione by mutableStateOf("")
        private set
    var categoria by mutableStateOf("")
        private set
    var fotoUri by mutableStateOf<Uri?>(null)
        private set
    var posizione by mutableStateOf<String?>(null)
        private set
    var invioConfermato by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set

    var titoloError by mutableStateOf<String?>(null)
        private set
    var descrizioneError by mutableStateOf<String?>(null)
        private set
    var categoriaError by mutableStateOf<String?>(null)
        private set
    var posizioneError by mutableStateOf<String?>(null)
        private set
    var submitError by mutableStateOf<String?>(null)
        private set

    val canSubmit: Boolean
        get() = !isLoading && isFormValid()

    fun onTitoloChange(value: String) {
        titolo = sanitizeText(value, maxTitoloLength)
        titoloError = validateTitolo(titolo)
    }

    fun onDescrizioneChange(value: String) {
        descrizione = sanitizeText(value, maxDescrizioneLength)
        descrizioneError = validateDescrizione(descrizione)
    }

    fun onCategoriaChange(value: String) {
        categoria = sanitizeText(value, 40)
        categoriaError = validateCategoria(categoria)
    }

    fun onFotoChange(uri: Uri?) { fotoUri = uri }

    fun onPosizioneChange(value: String?) {
        posizione = sanitizeText(value ?: "", 64).ifBlank { null }
        posizioneError = validatePosizione(posizione)
    }

    fun inviaSegnalazione() {
        submitError = null
        if (!validateForm()) {
            return
        }

        val normalizedTitle = titolo.lowercase()
        val areaKey = buildAreaKey(posizione.orEmpty())
        if (areaKey == null) {
            posizioneError = "Posizione non valida"
            return
        }

        isLoading = true
        val duplicateThreshold = Timestamp.now().seconds - duplicateWindowHours * 60 * 60

        firestore.collection("segnalazioni")
            .whereEqualTo("normalizedTitle", normalizedTitle)
            .whereEqualTo("areaKey", areaKey)
            .whereGreaterThan("createdAtEpochSeconds", duplicateThreshold)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    isLoading = false
                    submitError = "Esiste già una segnalazione simile nelle ultime 24 ore"
                    return@addOnSuccessListener
                }

                val segnalazionePayload = mapOf(
                    "titolo" to titolo,
                    "descrizione" to descrizione,
                    "categoria" to categoria,
                    "posizione" to posizione,
                    "normalizedTitle" to normalizedTitle,
                    "areaKey" to areaKey,
                    "fotoUri" to fotoUri?.toString(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "createdAtEpochSeconds" to Timestamp.now().seconds
                )

                firestore.collection("segnalazioni")
                    .add(segnalazionePayload)
                    .addOnSuccessListener {
                        isLoading = false
                        invioConfermato = true
                    }
                    .addOnFailureListener {
                        isLoading = false
                        submitError = "Invio non riuscito. Riprova."
                    }
            }
            .addOnFailureListener {
                isLoading = false
                submitError = "Controllo duplicati non riuscito. Riprova."
            }
    }

    fun resetConferma() {
        invioConfermato = false
    }

    private fun sanitizeText(value: String, maxLength: Int): String {
        return value
            .replace(Regex("[\\p{Cntrl}&&[^\\n\\t]]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(maxLength)
    }

    private fun validateForm(): Boolean {
        titoloError = validateTitolo(titolo)
        descrizioneError = validateDescrizione(descrizione)
        categoriaError = validateCategoria(categoria)
        posizioneError = validatePosizione(posizione)
        return isFormValid()
    }

    private fun isFormValid(): Boolean {
        return titoloError == null &&
            descrizioneError == null &&
            categoriaError == null &&
            posizioneError == null &&
            titolo.isNotBlank() &&
            descrizione.isNotBlank() &&
            categoria.isNotBlank() &&
            !posizione.isNullOrBlank()
    }

    private fun validateTitolo(value: String): String? {
        return when {
            value.isBlank() -> "Titolo obbligatorio"
            value.length < 3 -> "Titolo troppo corto"
            else -> null
        }
    }

    private fun validateDescrizione(value: String): String? {
        return when {
            value.isBlank() -> "Descrizione obbligatoria"
            value.length < 10 -> "Inserisci almeno 10 caratteri"
            else -> null
        }
    }

    private fun validateCategoria(value: String): String? {
        return if (value.isBlank()) "Categoria obbligatoria" else null
    }

    private fun validatePosizione(value: String?): String? {
        if (value.isNullOrBlank()) {
            return "Posizione obbligatoria"
        }

        val coordinates = parseCoordinates(value) ?: return "Posizione non valida"
        val isNearZero = abs(coordinates.first) < 0.0001 && abs(coordinates.second) < 0.0001
        return if (isNearZero) "Recupera una posizione reale" else null
    }

    private fun parseCoordinates(positionValue: String): Pair<Double, Double>? {
        val regex = Regex("Lat:\\s*([-+]?\\d+(?:\\.\\d+)?),\\s*Lng:\\s*([-+]?\\d+(?:\\.\\d+)?)")
        val match = regex.find(positionValue) ?: return null
        val lat = match.groupValues[1].toDoubleOrNull() ?: return null
        val lng = match.groupValues[2].toDoubleOrNull() ?: return null
        return lat to lng
    }

    private fun buildAreaKey(positionValue: String): String? {
        val (lat, lng) = parseCoordinates(positionValue) ?: return null
        val gridLat = (lat * 1000).roundToInt() / 1000.0
        val gridLng = (lng * 1000).roundToInt() / 1000.0
        return "$gridLat,$gridLng"
    }
}
