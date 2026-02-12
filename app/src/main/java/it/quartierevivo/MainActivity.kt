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
    private val mappaSegnalazioniViewModel: MappaSegnalazioniViewModel by viewModels()

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
