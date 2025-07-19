package it.quartierevivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import it.quartierevivo.ui.theme.QuartiereVivoTheme
import it.quartierevivo.MappaSegnalazioniScreen
import it.quartierevivo.MappaSegnalazioniViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val segnalazioneViewModel: SegnalazioneViewModel by viewModels()
    private val mappaViewModel: MappaSegnalazioniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuartiereVivoTheme {
                Surface {
                    // Switch to the map screen
                    MappaSegnalazioniScreen(mappaViewModel)
                }
            }
        }
    }
}
