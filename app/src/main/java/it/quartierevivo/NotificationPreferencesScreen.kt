package it.quartierevivo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationPreferencesScreen(
    viewModel: NotificationPreferencesViewModel,
    categories: List<String>,
    zones: List<String>,
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Preferenze notifiche", style = MaterialTheme.typography.titleLarge)
        Text("Categorie", style = MaterialTheme.typography.titleMedium)
        categories.forEach { category ->
            PreferenceCheckbox(
                label = category,
                checked = state.categorie.contains(category),
                onCheckedChange = { checked -> viewModel.setCategoryEnabled(category, checked) },
            )
        }

        Text("Zone", style = MaterialTheme.typography.titleMedium)
        zones.forEach { zone ->
            PreferenceCheckbox(
                label = zone,
                checked = state.zone.contains(zone),
                onCheckedChange = { checked -> viewModel.setZoneEnabled(zone, checked) },
            )
        }
    }
}

@Composable
private fun PreferenceCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}
