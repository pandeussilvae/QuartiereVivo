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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DettaglioSegnalazioneScreen(
    segnalazione: Segnalazione,
    onBack: () -> Unit,
    onAggiornaStato: (StatoSegnalazione) -> Boolean,
    dettaglioViewModel: DettaglioSegnalazioneViewModel = viewModel()
) {
    val ruoloUtente by dettaglioViewModel.ruoloUtenteCorrente.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var statoSelezionato by remember(segnalazione.status) { mutableStateOf(segnalazione.status) }
    var messaggioAggiornamento by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio segnalazione") },
                navigationIcon = {
                    Button(onClick = onBack) {
                        Text("Indietro")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(segnalazione.titolo, style = MaterialTheme.typography.headlineSmall)
            Text("Descrizione: ${segnalazione.descrizione.ifBlank { "Non disponibile" }}")
            Text("Categoria: ${segnalazione.categoria}")
            Text("Posizione: ${segnalazione.latitudine}, ${segnalazione.longitudine}")
            Text("Autore: ${segnalazione.autore.ifBlank { "Anonimo" }}")
            Text(
                "Data: ${segnalazione.dataCreazione.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}"
            )

            segnalazione.immagineUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Foto segnalazione",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Stato corrente: ${segnalazione.status.label}")
                    if (ruoloUtente != RuoloUtente.CITTADINO) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = statoSelezionato.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Nuovo stato") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            androidx.compose.material3.ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                StatoSegnalazione.entries.forEach { stato ->
                                    DropdownMenuItem(
                                        text = { Text(stato.label) },
                                        onClick = {
                                            statoSelezionato = stato
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Button(onClick = {
                            val esito = onAggiornaStato(statoSelezionato)
                            messaggioAggiornamento = if (esito) {
                                "Stato aggiornato correttamente"
                            } else {
                                "Non autorizzato ad aggiornare lo stato"
                            }
                        }) {
                            Text("Aggiorna stato")
                        }
                    } else {
                        Text("Solo moderatori e amministratori possono aggiornare lo stato")
                    }
                    messaggioAggiornamento?.let { Text(it) }
                }
            }

            Text("Storico aggiornamenti", style = MaterialTheme.typography.titleMedium)
            if (segnalazione.storicoAggiornamenti.isEmpty()) {
                Text("Nessun aggiornamento disponibile")
            } else {
                segnalazione.storicoAggiornamenti.forEach { storico ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "• ${storico.status.label} - ${storico.autore} (${storico.dataAggiornamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}) ${storico.nota}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}
