package it.quartierevivo

@Deprecated(
    message = "Usare it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel",
    replaceWith = ReplaceWith("it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel"),
)
typealias SegnalazioneViewModel = it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel
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

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

private const val PRIVACY_CONSENT_TEXT =
    "Acconsento all'uso della mia posizione solo per associare la segnalazione al punto corretto sulla mappa del quartiere."

enum class PermissionState {
    Unknown,
    Granted,
    Denied,
    PermanentlyDenied,
}

data class SegnalazioneUiState(
    val titolo: String = "",
    val descrizione: String = "",
    val categoria: String = "",
    val fotoUri: Uri? = null,
    val posizione: String? = null,
    val invioConfermato: Boolean = false,
    val cameraPermissionState: PermissionState = PermissionState.Unknown,
    val locationPermissionState: PermissionState = PermissionState.Unknown,
    val privacyConsentGiven: Boolean = false,
    val privacyConsentText: String = PRIVACY_CONSENT_TEXT,
)

class SegnalazioneViewModel : ViewModel() {
    var uiState by mutableStateOf(SegnalazioneUiState())
    var titolo by mutableStateOf("")
        private set
    var descrizione by mutableStateOf("")
        private set
    var categoria by mutableStateOf("")
        private set
    var fotoUri by mutableStateOf<Uri?>(null)
        private set
    var lat by mutableStateOf<Double?>(null)
        private set
    var lng by mutableStateOf<Double?>(null)
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
    var erroreInvio by mutableStateOf<String?>(null)
        private set

    private var pendingRetry: (() -> Unit)? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun onTitoloChange(value: String) {
        uiState = uiState.copy(titolo = value)
    }

    fun onDescrizioneChange(value: String) {
        uiState = uiState.copy(descrizione = value)
    }

    fun onCategoriaChange(value: String) {
        uiState = uiState.copy(categoria = value)
    }

    fun onFotoChange(uri: Uri?) {
        uiState = uiState.copy(fotoUri = uri)
    }

    fun onPosizioneChange(value: String?) {
        uiState = uiState.copy(posizione = value)
    }

    fun onPrivacyConsentChange(consentGiven: Boolean) {
        uiState = uiState.copy(privacyConsentGiven = consentGiven)
    }

    fun onCameraPermissionStateChange(permissionState: PermissionState) {
        uiState = uiState.copy(cameraPermissionState = permissionState)
    }

    fun onLocationPermissionStateChange(permissionState: PermissionState) {
        uiState = uiState.copy(locationPermissionState = permissionState)
    }

    fun inviaSegnalazione() {
        uiState = uiState.copy(invioConfermato = true)
    fun onTitoloChange(value: String) { titolo = value }
    fun onDescrizioneChange(value: String) { descrizione = value }
    fun onCategoriaChange(value: String) { categoria = value }
    fun onFotoChange(uri: Uri?) { fotoUri = uri }
    fun onPosizioneChange(latitudine: Double, longitudine: Double) {
        lat = latitudine
        lng = longitudine
    }

    fun inviaSegnalazione() {
        val latitudine = lat
        val longitudine = lng
        if (titolo.isBlank() || descrizione.isBlank() || categoria.isBlank() || latitudine == null || longitudine == null) {
            erroreInvio = "Compila tutti i campi obbligatori prima dell'invio"
            pendingRetry = null
            return
        }

        val retryAction = { inviaSegnalazione() }
        pendingRetry = retryAction

        viewModelScope.launch {
            isLoading = true
            erroreInvio = null
            invioConfermato = false

            runCatching {
                val imageUrl = fotoUri?.let { uploadImage(it) }
                val document = firestore.collection(Segnalazione.COLLECTION).document()
                val uid = auth.currentUser?.uid ?: "anonimo"
                val payload = Segnalazione(
                    id = document.id,
                    titolo = titolo.trim(),
                    descrizione = descrizione.trim(),
                    categoria = categoria,
                    lat = latitudine,
                    lng = longitudine,
                    imageUrl = imageUrl,
                    status = Segnalazione.STATUS_OPEN,
                    createdBy = uid,
                ).toFirestorePayload()

                document.set(payload).await()
            }.onSuccess {
                invioConfermato = true
                pendingRetry = null
                clearForm()
            }.onFailure { throwable ->
                erroreInvio = throwable.message ?: "Errore durante il caricamento della segnalazione"
            }

            isLoading = false
        }
    }

    fun retryInvio() {
        pendingRetry?.invoke()
    }

    fun dismissErrore() {
        erroreInvio = null
        Firebase.analytics.logEvent("submit_report") {
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, categoria)
            param(FirebaseAnalytics.Param.ITEM_NAME, titolo)
        }
        invioConfermato = true
    }

    fun resetConferma() {
        uiState = uiState.copy(invioConfermato = false)
    }

    private suspend fun uploadImage(uri: Uri): String {
        val extension = "jpg"
        val imageRef = storage.reference
            .child("segnalazioni")
            .child("${UUID.randomUUID()}.$extension")

        imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }

    private fun clearForm() {
        titolo = ""
        descrizione = ""
        categoria = ""
        fotoUri = null
        lat = null
        lng = null
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
