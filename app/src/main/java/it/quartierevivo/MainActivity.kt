package it.quartierevivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import it.quartierevivo.data.remote.FirebaseAuthService
import it.quartierevivo.data.remote.FirestoreService
import it.quartierevivo.data.remote.StorageService
import it.quartierevivo.data.repository.FirebaseSegnalazioneRepository
import it.quartierevivo.domain.usecase.InviaSegnalazioneUseCase
import it.quartierevivo.domain.usecase.ObserveSegnalazioniUseCase
import it.quartierevivo.domain.usecase.SeedSegnalazioniUseCase
import it.quartierevivo.presentation.AppViewModelFactory
import it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel
import it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel
import it.quartierevivo.ui.theme.QuartiereVivoTheme

class MainActivity : ComponentActivity() {
    private val appViewModelFactory by lazy {
        val firestoreService = FirestoreService(FirebaseFirestore.getInstance())
        val storageService = StorageService(FirebaseStorage.getInstance())
        val authService = FirebaseAuthService(FirebaseAuth.getInstance())
        val repository = FirebaseSegnalazioneRepository(firestoreService, storageService, authService)

        AppViewModelFactory(
            inviaSegnalazioneUseCase = InviaSegnalazioneUseCase(repository),
            observeSegnalazioniUseCase = ObserveSegnalazioniUseCase(repository),
            seedSegnalazioniUseCase = SeedSegnalazioniUseCase(repository),
        )
    }

    private val segnalazioneViewModel: SegnalazioneViewModel by viewModels { appViewModelFactory }
    private val mappaSegnalazioniViewModel: MappaSegnalazioniViewModel by viewModels { appViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuartiereVivoTheme {
                Surface {
                    MappaSegnalazioniScreen(mappaSegnalazioniViewModel)
                    // SegnalazioneScreen(segnalazioneViewModel)
                }
            }
        }
    }
}
