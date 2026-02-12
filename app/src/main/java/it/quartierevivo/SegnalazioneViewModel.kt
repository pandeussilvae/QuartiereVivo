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

class SegnalazioneViewModel : ViewModel() {
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
    var erroreInvio by mutableStateOf<String?>(null)
        private set

    private var pendingRetry: (() -> Unit)? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
        invioConfermato = false
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
}
