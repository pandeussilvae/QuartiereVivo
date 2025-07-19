package it.quartierevivo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Bottom sheet con i dettagli di una segnalazione.
 */
@Composable
fun DettaglioSegnalazioneSheet(
    segnalazione: Segnalazione,
    onChiudi: () -> Unit,
    onSegnalaRisolta: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(segnalazione.titolo, style = MaterialTheme.typography.titleMedium)
        Text(segnalazione.descrizione)
        Text("Categoria: ${segnalazione.categoria}")
        segnalazione.fotoUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        Text("Inviata il ${segnalazione.dataInvio}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onChiudi, modifier = Modifier.fillMaxWidth()) {
            Text("Chiudi")
        }
        Button(onClick = onSegnalaRisolta, modifier = Modifier.fillMaxWidth()) {
            Text("Segnala come risolta")
        }
    }
}
