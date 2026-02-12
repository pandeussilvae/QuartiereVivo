package it.quartierevivo

import androidx.compose.runtime.Composable

@Deprecated(
    message = "Usare it.quartierevivo.presentation.mappa.MappaSegnalazioniScreen",
    replaceWith = ReplaceWith("it.quartierevivo.presentation.mappa.MappaSegnalazioniScreen(viewModel, onOpenDetails, onOpenPreferences)"),
)
@Composable
fun MappaSegnalazioniScreen(
    viewModel: it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel,
    onOpenDetails: (String) -> Unit,
    onOpenPreferences: () -> Unit,
) {
    it.quartierevivo.presentation.mappa.MappaSegnalazioniScreen(
        viewModel = viewModel,
        onOpenDetails = onOpenDetails,
        onOpenPreferences = onOpenPreferences,
    )
}
