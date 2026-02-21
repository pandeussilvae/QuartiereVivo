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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.quartierevivo.R
import it.quartierevivo.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MappaSegnalazioniScreen(
    viewModel: MappaSegnalazioniViewModel = viewModel(),
    onDettaglioClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onOpenPreferences: () -> Unit,
    onOpenReportForm: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val categoriaFiltro by viewModel.getCategoriaFiltro().collectAsState()
    val segnalazioni = (uiState as? UiState.Success)?.data.orEmpty()
    val categorie = segnalazioni.map { it.categoria }.filter { it.isNotBlank() }.distinct()
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TextButton(onClick = onLogoutClick) { Text(stringResource(R.string.logout)) }
                TextButton(onClick = onOpenReportForm) { Text(stringResource(R.string.open_report_form)) }
                TextButton(onClick = onOpenPreferences) { Text(stringResource(R.string.open_preferences)) }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = stringResource(R.string.reports_title), style = MaterialTheme.typography.titleLarge)

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = categoriaFiltro.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.filter_category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                androidx.compose.material3.ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.filter_all)) },
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
                UiState.Loading -> Text(stringResource(R.string.reports_loading))
                UiState.Empty -> Text(stringResource(R.string.reports_empty))
                is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is UiState.Success -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.data, key = { it.id }) { segnalazione ->
                            TextButton(
                                onClick = { onDettaglioClick(segnalazione.id) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(stringResource(R.string.report_row_title, segnalazione.titolo, segnalazione.categoria))
                            }
                        }
                    }
                }
            }
        }
    }
}
