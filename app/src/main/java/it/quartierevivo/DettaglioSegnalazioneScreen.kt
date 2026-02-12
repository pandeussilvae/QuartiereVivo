package it.quartierevivo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DettaglioSegnalazioneScreen(segnalazione: Segnalazione?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (segnalazione == null) {
            Text("Segnalazione non trovata", style = MaterialTheme.typography.titleMedium)
            return@Column
        }

        Text(text = segnalazione.titolo, style = MaterialTheme.typography.titleLarge)
        Text(text = "Stato: ${segnalazione.status}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Categoria: ${segnalazione.categoria}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Zona: ${segnalazione.zona}", style = MaterialTheme.typography.bodyMedium)
        if (segnalazione.descrizione.isNotBlank()) {
            Text(text = segnalazione.descrizione, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
