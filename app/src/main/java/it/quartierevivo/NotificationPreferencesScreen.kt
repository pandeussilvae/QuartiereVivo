package it.quartierevivo

import androidx.compose.runtime.Composable

@Deprecated(
    message = "Usare it.quartierevivo.presentation.notification.NotificationPreferencesScreen",
    replaceWith = ReplaceWith("it.quartierevivo.presentation.notification.NotificationPreferencesScreen(viewModel, categories, zones)"),
)
@Composable
fun NotificationPreferencesScreen(
    viewModel: it.quartierevivo.presentation.notification.NotificationPreferencesViewModel,
    categories: List<String>,
    zones: List<String>,
) {
    it.quartierevivo.presentation.notification.NotificationPreferencesScreen(
        viewModel = viewModel,
        categories = categories,
        zones = zones,
    )
}
