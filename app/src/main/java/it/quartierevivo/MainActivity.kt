package it.quartierevivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
        viewModel.tracciaLogin()
        mappaSegnalazioniViewModel.aggiornaSegnalazioni(
            listOf(
                Segnalazione("1", getString(R.string.sample_report_light), 45.4641, 9.1916, null, getString(R.string.report_category_maintenance)),
                Segnalazione("2", getString(R.string.sample_report_waste), 45.4650, 9.1890, null, getString(R.string.report_category_other)),
                Segnalazione("3", getString(R.string.sample_report_danger), 45.4630, 9.1880, null, getString(R.string.report_category_safety))
            )
        )
        setContent {
            QuartiereVivoTheme {
                Surface {
                    var schermataCorrente by mutableStateOf(Schermata.Mappa)
                    when (schermataCorrente) {
                        Schermata.Mappa -> MappaSegnalazioniScreen(
                            viewModel = mappaSegnalazioniViewModel,
                            onOpenReportForm = { schermataCorrente = Schermata.Segnalazione },
                            onOpenPrivacy = { schermataCorrente = Schermata.Privacy }
                        )

                        Schermata.Segnalazione -> SegnalazioneScreen(segnalazioneViewModel)
                        Schermata.Privacy -> PrivacyTerminiScreen(onBack = { schermataCorrente = Schermata.Mappa })
                    }
                }
            }
        }
    }
}

private enum class Schermata {
    Mappa,
    Segnalazione,
    Privacy
}
