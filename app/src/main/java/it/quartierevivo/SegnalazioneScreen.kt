package it.quartierevivo

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegnalazioneScreen(viewModel: SegnalazioneViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onFotoChange(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            photoLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permesso fotocamera negato", Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            // Placeholder for real GPS retrieval
            viewModel.onPosizioneChange("Lat:45.4642, Lng:9.1900")
        } else {
            Toast.makeText(context, "Permesso posizione negato", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(viewModel.invioConfermato) {
        if (viewModel.invioConfermato) {
            snackbarHostState.showSnackbar("Segnalazione inviata")
            viewModel.resetConferma()
        }
    }

    var expanded by remember { mutableStateOf(false) }
    val categorie = listOf("Manutenzione", "Sicurezza", "Altro")

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = viewModel.titolo,
                onValueChange = viewModel::onTitoloChange,
                label = { Text("Titolo") },
                isError = viewModel.titoloError != null,
                supportingText = {
                    val error = viewModel.titoloError
                    if (error != null) {
                        Text(error)
                    } else {
                        Text("${viewModel.titolo.length}/80")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.descrizione,
                onValueChange = viewModel::onDescrizioneChange,
                label = { Text("Descrizione") },
                isError = viewModel.descrizioneError != null,
                supportingText = {
                    val error = viewModel.descrizioneError
                    if (error != null) {
                        Text(error)
                    } else {
                        Text("${viewModel.descrizione.length}/500")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = viewModel.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    isError = viewModel.categoriaError != null,
                    supportingText = {
                        viewModel.categoriaError?.let { Text(it) }
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                androidx.compose.material3.ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorie.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.onCategoriaChange(cat)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Button(onClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text("Seleziona foto")
            }
            Button(onClick = {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }) {
                Text("Ottieni posizione")
            }
            viewModel.posizioneError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            viewModel.submitError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = { viewModel.inviaSegnalazione() },
                enabled = viewModel.canSubmit
            ) {
                Text(if (viewModel.isLoading) "Invio in corso..." else "Invia")
            }
        }
    }
}
