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
    var posizione by mutableStateOf<String?>(null)
        private set
    var invioConfermato by mutableStateOf(false)
        private set

    fun onTitoloChange(value: String) { titolo = value }
    fun onDescrizioneChange(value: String) { descrizione = value }
    fun onCategoriaChange(value: String) { categoria = value }
    fun onFotoChange(uri: Uri?) { fotoUri = uri }
    fun onPosizioneChange(value: String?) { posizione = value }

    fun inviaSegnalazione() {
        Firebase.analytics.logEvent("submit_report") {
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, categoria)
            param(FirebaseAnalytics.Param.ITEM_NAME, titolo)
        }
        invioConfermato = true
    }

    fun resetConferma() {
        invioConfermato = false
    }
}
