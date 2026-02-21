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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.quartierevivo.QuartiereVivoMessagingService.Companion.KEY_SEGNALAZIONE_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import it.quartierevivo.data.remote.FirebaseAuthService
import it.quartierevivo.data.remote.FirestoreService
import it.quartierevivo.data.remote.StorageService
import it.quartierevivo.data.repository.FirebaseSegnalazioneRepository
import it.quartierevivo.data.repository.NotificationPreferencesRepository
import it.quartierevivo.domain.usecase.InviaSegnalazioneUseCase
import it.quartierevivo.domain.usecase.ObserveSegnalazioniUseCase
import it.quartierevivo.domain.usecase.SeedSegnalazioniUseCase
import it.quartierevivo.presentation.AppViewModelFactory
import it.quartierevivo.presentation.common.UiState
import it.quartierevivo.presentation.dettaglio.DettaglioSegnalazioneScreen
import it.quartierevivo.presentation.mappa.MappaSegnalazioniScreen
import it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel
import it.quartierevivo.presentation.notification.NotificationPreferencesScreen
import it.quartierevivo.presentation.notification.NotificationPreferencesViewModel
import it.quartierevivo.presentation.notification.NotificationPreferencesViewModelFactory
import it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel
import it.quartierevivo.ui.theme.QuartiereVivoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
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
    private val mappaSegnalazioniViewModel: MappaSegnalazioniViewModel by viewModels { appViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleDeepLink(intent)

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

                val mappaUiState by mappaSegnalazioniViewModel.uiState.collectAsState()
                val segnalazioni = (mappaUiState as? UiState.Success)?.data.orEmpty()
                val segnalazioneViewModel: SegnalazioneViewModel = viewModel(factory = appViewModelFactory)

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
                            onDettaglioClick = { id -> navController.navigate("dettaglio/$id") },
                            onLogoutClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("mappa")
                            },
                            onOpenPreferences = { navController.navigate("preferenze_notifiche") },
                            onOpenReportForm = { navController.navigate("segnalazione") },
                        )
                    }
                    composable("segnalazione") {
                        SegnalazioneScreen(viewModel = segnalazioneViewModel)
                    }
                    composable(
                        route = "dettaglio/{segnalazioneId}",
                        arguments = listOf(navArgument("segnalazioneId") { type = NavType.StringType }),
                    ) { backStackEntry ->
                        val segnalazioneId = backStackEntry.arguments?.getString("segnalazioneId")
                        val segnalazione = segnalazioni.firstOrNull { it.id == segnalazioneId }
                        DettaglioSegnalazioneScreen(segnalazione = segnalazione)
                    }
                    composable("preferenze_notifiche") {
                        NotificationPreferencesScreen(
                            viewModel = preferencesViewModel,
                            categories = segnalazioni.map { it.categoria }.distinct(),
                            zones = emptyList(),
                        )
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
}
