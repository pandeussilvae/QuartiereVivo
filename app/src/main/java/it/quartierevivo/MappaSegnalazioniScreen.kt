package it.quartierevivo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

/**
 * Schermata che mostra le segnalazioni su mappa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MappaSegnalazioniScreen(
    viewModel: MappaSegnalazioniViewModel = viewModel()
) {
    val selected by viewModel.segnalazioneSelezionata

    if (selected != null) {
        ModalBottomSheet(onDismissRequest = viewModel::chiudiDettaglio) {
            DettaglioSegnalazioneSheet(
                segnalazione = selected!!,
                onChiudi = viewModel::chiudiDettaglio,
                onSegnalaRisolta = viewModel::segnalaRisolta
            )
        }
    }

    GoogleMap(modifier = Modifier.fillMaxSize()) {
        viewModel.segnalazioni.forEach { segnalazione ->
            segnalazione.posizione?.let { pos ->
                Marker(
                    state = rememberMarkerState(position = pos),
                    title = segnalazione.titolo,
                    onClick = {
                        viewModel.onMarkerSelected(segnalazione)
                        true
                    }
                )
            }
        }
    }
}
