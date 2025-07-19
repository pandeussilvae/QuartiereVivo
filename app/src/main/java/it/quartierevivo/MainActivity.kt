package it.quartierevivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import it.quartierevivo.ui.theme.QuartiereVivoTheme
import it.quartierevivo.MappaSegnalazioniViewModel
import it.quartierevivo.Segnalazione

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val segnalazioneViewModel: SegnalazioneViewModel by viewModels()
    private val mappaSegnalazioniViewModel: MappaSegnalazioniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mappaSegnalazioniViewModel.aggiornaSegnalazioni(
            listOf(
                Segnalazione("1", "Guasto illuminazione", 45.4641, 9.1916, null, "Manutenzione"),
                Segnalazione("2", "Rifiuti abbandonati", 45.4650, 9.1890, null, "Altro"),
                Segnalazione("3", "Area pericolosa", 45.4630, 9.1880, null, "Sicurezza")
            )
        )
        setContent {
            QuartiereVivoTheme {
                Surface {
                    MappaSegnalazioniScreen(mappaSegnalazioniViewModel)
                }
            }
        }
    }
}
