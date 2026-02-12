package it.quartierevivo

import androidx.compose.runtime.Composable
import it.quartierevivo.domain.model.Segnalazione

@Deprecated(
    message = "Usare it.quartierevivo.presentation.dettaglio.DettaglioSegnalazioneScreen",
    replaceWith = ReplaceWith("it.quartierevivo.presentation.dettaglio.DettaglioSegnalazioneScreen(segnalazione)"),
)
@Composable
fun DettaglioSegnalazioneScreen(segnalazione: Segnalazione?) {
    it.quartierevivo.presentation.dettaglio.DettaglioSegnalazioneScreen(segnalazione)
}
