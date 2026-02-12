package it.quartierevivo.presentation.mappa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.quartierevivo.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MappaSegnalazioniScreen(
    viewModel: MappaSegnalazioniViewModel = viewModel(),
    onOpenDetails: (String) -> Unit,
    onOpenPreferences: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val categoriaFiltro by viewModel.getCategoriaFiltro().collectAsState()
    val segnalazioni = (uiState as? UiState.Success)?.data.orEmpty()
    val categorie = segnalazioni.map { it.categoria }.filter { it.isNotBlank() }.distinct()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Segnalazioni", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onOpenPreferences) {
                Text("Preferenze notifiche")
            }
        }

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = categoriaFiltro.orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtra categoria") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
            )
            androidx.compose.material3.ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Tutte") },
                    onClick = {
                        viewModel.setCategoriaFiltro(null)
                        expanded = false
                    },
                )
                categorie.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria) },
                        onClick = {
                            viewModel.setCategoriaFiltro(categoria)
                            expanded = false
                        },
                    )
                }
            }
        }

        when (val state = uiState) {
            UiState.Loading -> Text("Caricamento segnalazioni…")
            UiState.Empty -> Text("Nessuna segnalazione disponibile")
            is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.data, key = { it.id }) { segnalazione ->
                        TextButton(
                            onClick = { onOpenDetails(segnalazione.id) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("${segnalazione.titolo} · ${segnalazione.categoria}")
                        }
                    }
                }
            }
        }
    }
}
