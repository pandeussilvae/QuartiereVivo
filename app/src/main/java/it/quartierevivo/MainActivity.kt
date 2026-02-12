package it.quartierevivo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.quartierevivo.QuartiereVivoMessagingService.Companion.KEY_SEGNALAZIONE_ID
import it.quartierevivo.ui.theme.QuartiereVivoTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import it.quartierevivo.ui.theme.QuartiereVivoTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import it.quartierevivo.ui.theme.QuartiereVivoTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val segnalazioneViewModel: SegnalazioneViewModel by viewModels()
    private val mappaSegnalazioniViewModel: MappaSegnalazioniViewModel by viewModels()
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

        handleDeepLink(intent)
        seedData()

        lifecycleScope.launch(Dispatchers.IO) {
            FcmTokenManager().registerCurrentToken()
        }

        setContent {
            QuartiereVivoTheme {
                val navController = rememberNavController()
                val pendingDeepLink by mainViewModel.pendingSegnalazioneDeepLink.collectAsState()
                val context = LocalContext.current
                val preferencesViewModel: NotificationPreferencesViewModel = viewModel(
                    factory = NotificationPreferencesViewModelFactory(
                        NotificationPreferencesRepository(context.applicationContext),
                    ),
                )

                LaunchedEffect(pendingDeepLink) {
                    pendingDeepLink?.let { id ->
                        navController.navigate("dettaglio/$id")
                        mainViewModel.consumeDeepLink()
                    }
                }

                NavHost(navController = navController, startDestination = "mappa") {
                    composable("mappa") {
                        MappaSegnalazioniScreen(
                            viewModel = mappaSegnalazioniViewModel,
                            onOpenDetails = { id -> navController.navigate("dettaglio/$id") },
                            onOpenPreferences = { navController.navigate("preferenze_notifiche") },
                        )
                    }
                    composable(
                        route = "dettaglio/{segnalazioneId}",
                        arguments = listOf(navArgument("segnalazioneId") { type = NavType.StringType }),
                    ) { backStackEntry ->
                        val segnalazioneId = backStackEntry.arguments?.getString("segnalazioneId")
                        val segnalazione = segnalazioneId?.let { mappaSegnalazioniViewModel.getSegnalazioneById(it) }
                        DettaglioSegnalazioneScreen(segnalazione = segnalazione)
                    }
                    composable("preferenze_notifiche") {
                        NotificationPreferencesScreen(
                            viewModel = preferencesViewModel,
                            categories = mappaSegnalazioniViewModel.segnalazioni.value.map { it.categoria }.distinct(),
                            zones = mappaSegnalazioniViewModel.segnalazioni.value.map { it.zona }.distinct(),
                        )
        setContent {
            QuartiereVivoTheme {
                Surface {
                    MappaSegnalazioniScreen(mappaSegnalazioniViewModel)
                    // SegnalazioneScreen(segnalazioneViewModel)
        viewModel.tracciaLogin()
        mappaSegnalazioniViewModel.aggiornaSegnalazioni(
            listOf(
                Segnalazione(
                    id = "1",
                    titolo = "Guasto illuminazione",
                    latitudine = 45.4641,
                    longitudine = 9.1916,
                    descrizione = "Lampione spento in via Garibaldi da 3 giorni",
                    categoria = "Manutenzione",
                    autore = "Comitato Zona Nord",
                    dataCreazione = LocalDateTime.now().minusDays(2)
                ),
                Segnalazione(
                    id = "2",
                    titolo = "Rifiuti abbandonati",
                    latitudine = 45.4650,
                    longitudine = 9.1890,
                    descrizione = "Sacchi lasciati vicino al parco giochi",
                    categoria = "Altro",
                    autore = "Mario Rossi",
                    dataCreazione = LocalDateTime.now().minusDays(1),
                    status = StatoSegnalazione.IN_CARICO,
                    storicoAggiornamenti = listOf(
                        AggiornamentoStato(
                            status = StatoSegnalazione.IN_CARICO,
                            autore = "Moderatore Area Ovest",
                            dataAggiornamento = LocalDateTime.now().minusHours(4),
                            nota = "Intervento programmato"
                        )
                    )
                ),
                Segnalazione(
                    id = "3",
                    titolo = "Area pericolosa",
                    latitudine = 45.4630,
                    longitudine = 9.1880,
                    descrizione = "Buca profonda sul marciapiede",
                    categoria = "Sicurezza",
                    autore = "Giulia Bianchi",
                    dataCreazione = LocalDateTime.now().minusHours(18)
                )
                Segnalazione("1", getString(R.string.sample_report_light), 45.4641, 9.1916, null, getString(R.string.report_category_maintenance)),
                Segnalazione("2", getString(R.string.sample_report_waste), 45.4650, 9.1890, null, getString(R.string.report_category_other)),
                Segnalazione("3", getString(R.string.sample_report_danger), 45.4630, 9.1880, null, getString(R.string.report_category_safety))
            )
        )

        setContent {
            QuartiereVivoTheme {
                Surface {
                    var segnalazioneSelezionataId by remember { mutableStateOf<String?>(null) }
                    val dettaglioViewModel: DettaglioSegnalazioneViewModel = viewModel()
                    dettaglioViewModel.setRuoloUtente(RuoloUtente.MODERATORE)

                    val segnalazioneSelezionata = segnalazioneSelezionataId?.let {
                        mappaSegnalazioniViewModel.getSegnalazioneById(it)
                    }

                    if (segnalazioneSelezionata == null) {
                        MappaSegnalazioniScreen(
                            viewModel = mappaSegnalazioniViewModel,
                            onDettaglioClick = { segnalazioneId ->
                                segnalazioneSelezionataId = segnalazioneId
                            }
                        )
                    } else {
                        DettaglioSegnalazioneScreen(
                            segnalazione = segnalazioneSelezionata,
                            onBack = { segnalazioneSelezionataId = null },
                            onAggiornaStato = { nuovoStato ->
                                mappaSegnalazioniViewModel.aggiornaStatusSegnalazione(
                                    id = segnalazioneSelezionata.id,
                                    nuovoStatus = nuovoStato,
                                    ruoloUtente = dettaglioViewModel.ruoloUtenteCorrente.value,
                                    autoreAggiornamento = "Moderatore Area Ovest"
                                )
                            },
                            dettaglioViewModel = dettaglioViewModel
                        )
                    val authState by viewModel.authState.collectAsState()
                    val authUiState by authViewModel.uiState.collectAsState()
                    var authScreen by rememberSaveable { mutableStateOf(AuthScreen.Login) }

                    when (authState) {
                        AuthState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        AuthState.Authenticated -> {
                            MappaSegnalazioniScreen(
                                viewModel = mappaSegnalazioniViewModel,
                                onLogoutClick = viewModel::signOut
                            )
                        }

                        AuthState.Unauthenticated -> {
                            when (authScreen) {
                                AuthScreen.Login -> LoginScreen(
                                    uiState = authUiState,
                                    onEmailChange = authViewModel::onEmailChanged,
                                    onPasswordChange = authViewModel::onPasswordChanged,
                                    onLoginClick = authViewModel::login,
                                    onNavigateToRegister = { authScreen = AuthScreen.Register },
                                    onNavigateToForgotPassword = { authScreen = AuthScreen.ForgotPassword }
                                )

                                AuthScreen.Register -> RegisterScreen(
                                    uiState = authUiState,
                                    onEmailChange = authViewModel::onEmailChanged,
                                    onPasswordChange = authViewModel::onPasswordChanged,
                                    onConfirmPasswordChange = authViewModel::onConfirmPasswordChanged,
                                    onRegisterClick = authViewModel::register,
                                    onNavigateToLogin = { authScreen = AuthScreen.Login }
                                )

                                AuthScreen.ForgotPassword -> ForgotPasswordScreen(
                                    uiState = authUiState,
                                    onEmailChange = authViewModel::onEmailChanged,
                                    onResetPasswordClick = authViewModel::sendPasswordReset,
                                    onNavigateToLogin = { authScreen = AuthScreen.Login }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class AuthScreen {
    Login,
    Register,
    ForgotPassword
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val segnalazioneId = intent?.getStringExtra(KEY_SEGNALAZIONE_ID)
            ?: intent?.data?.lastPathSegment
        mainViewModel.onDeepLinkReceived(segnalazioneId)
    }

    private fun seedData() {
        mappaSegnalazioniViewModel.aggiornaSegnalazioni(
            listOf(
                Segnalazione(
                    id = "1",
                    titolo = "Guasto illuminazione",
                    descrizione = "Lampione spento in via Roma",
                    latitudine = 45.4641,
                    longitudine = 9.1916,
                    categoria = "Manutenzione",
                    zona = "Centro",
                    status = "In lavorazione",
                ),
                Segnalazione(
                    id = "2",
                    titolo = "Rifiuti abbandonati",
                    descrizione = "Sacchi vicino al parco giochi",
                    latitudine = 45.4650,
                    longitudine = 9.1890,
                    categoria = "Altro",
                    zona = "Nord",
                    status = "Aperta",
                ),
                Segnalazione(
                    id = "3",
                    titolo = "Area pericolosa",
                    descrizione = "Buca profonda su marciapiede",
                    latitudine = 45.4630,
                    longitudine = 9.1880,
                    categoria = "Sicurezza",
                    zona = "Sud",
                    status = "Chiusa",
                ),
            ),
        )
    }
}

private enum class Schermata {
    Mappa,
    Segnalazione,
    Privacy
}
