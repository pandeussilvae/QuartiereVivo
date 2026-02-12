package it.quartierevivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import it.quartierevivo.ui.theme.QuartiereVivoTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    private val mappaSegnalazioniViewModel: MappaSegnalazioniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    }
                }
            }
        }
    }
}
