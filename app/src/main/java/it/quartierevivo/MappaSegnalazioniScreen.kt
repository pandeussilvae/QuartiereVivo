package it.quartierevivo

import androidx.compose.runtime.Composable

@Deprecated(
    message = "Usare it.quartierevivo.presentation.mappa.MappaSegnalazioniScreen",
    replaceWith = ReplaceWith(
        "it.quartierevivo.presentation.mappa.MappaSegnalazioniScreen(viewModel, onDettaglioClick, onLogoutClick, onOpenPreferences, onOpenReportForm)",
    ),
)
@Composable
fun MappaSegnalazioniScreen(
    viewModel: it.quartierevivo.presentation.mappa.MappaSegnalazioniViewModel,
    onDettaglioClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onOpenPreferences: () -> Unit,
    onOpenReportForm: () -> Unit,
) {
    it.quartierevivo.presentation.mappa.MappaSegnalazioniScreen(
        viewModel = viewModel,
        onDettaglioClick = onDettaglioClick,
        onLogoutClick = onLogoutClick,
        onOpenPreferences = onOpenPreferences,
        onOpenReportForm = onOpenReportForm,
    )
}
