package it.quartierevivo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import it.quartierevivo.presentation.common.UiState
import it.quartierevivo.presentation.segnalazione.SegnalazioneViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegnalazioneScreen(viewModel: SegnalazioneViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val submitState by viewModel.uiState.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val categorie = listOf(
        stringResource(R.string.report_category_maintenance),
        stringResource(R.string.report_category_safety),
        stringResource(R.string.report_category_other),
    )

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.onFotoChange(uri)
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            photoLauncher.launch("image/*")
        } else {
            Toast.makeText(context, context.getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            fetchCurrentLocation(
                context = context,
                onLocationReceived = { location ->
                    viewModel.onPosizioneChange("Lat:${location.latitude}, Lng:${location.longitude}")
                },
                onLocationError = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.location_unavailable))
                    }
                },
            )
        } else {
            Toast.makeText(context, context.getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(submitState) {
        when (val state = submitState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar(context.getString(R.string.report_sent))
                viewModel.resetUiState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetUiState()
            }
            UiState.Empty,
            UiState.Loading,
            -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = viewModel.titolo,
                onValueChange = viewModel::onTitoloChange,
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = viewModel.descrizione,
                onValueChange = viewModel::onDescrizioneChange,
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
            )

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = viewModel.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                androidx.compose.material3.ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    categorie.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.onCategoriaChange(cat)
                                expanded = false
                            },
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (hasPermission(context, Manifest.permission.CAMERA)) {
                        photoLauncher.launch("image/*")
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text(stringResource(R.string.select_photo))
                }

                Button(onClick = {
                    if (hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        fetchCurrentLocation(
                            context = context,
                            onLocationReceived = { location ->
                                viewModel.onPosizioneChange("Lat:${location.latitude}, Lng:${location.longitude}")
                            },
                            onLocationError = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(context.getString(R.string.location_unavailable))
                                }
                            },
                        )
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }) {
                    Text(stringResource(R.string.get_location))
                }
            }

            viewModel.posizione?.let { posizione ->
                Text(text = stringResource(R.string.detected_position, posizione))
            }

            Button(
                onClick = viewModel::inviaSegnalazione,
                enabled = submitState !is UiState.Loading,
            ) {
                if (submitState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                }
                Text(
                    text = if (submitState is UiState.Loading) {
                        stringResource(R.string.sending)
                    } else {
                        stringResource(R.string.send)
                    },
                )
            }
        }
    }
}

private fun hasPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

private fun fetchCurrentLocation(
    context: Context,
    onLocationReceived: (Location) -> Unit,
    onLocationError: () -> Unit,
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    @Suppress("MissingPermission")
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location)
            } else {
                onLocationError()
            }
        }
        .addOnFailureListener {
            onLocationError()
        }
}
