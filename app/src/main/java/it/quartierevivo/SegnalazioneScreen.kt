package it.quartierevivo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegnalazioneScreen(viewModel: SegnalazioneViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onFotoChange(uri)
    }

    fun updateCameraPermissionState(granted: Boolean) {
        if (granted) {
            viewModel.onCameraPermissionStateChange(PermissionState.Granted)
            photoLauncher.launch("image/*")
        } else {
            val isPermanentDenied = activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
            } == true
            viewModel.onCameraPermissionStateChange(
                if (isPermanentDenied) PermissionState.PermanentlyDenied else PermissionState.Denied
            )
        }
    }

    fun updateLocationPermissionState(granted: Boolean) {
        if (granted) {
            viewModel.onLocationPermissionStateChange(PermissionState.Granted)
            if (uiState.privacyConsentGiven) {
                fetchCurrentLocation(
                    context = context,
                    onLocationReceived = { location ->
                        viewModel.onPosizioneChange("Lat:${location.latitude}, Lng:${location.longitude}")
                    },
                    onLocationError = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Impossibile ottenere la posizione corrente")
                        }
                    }
                )
            }
        } else {
            val isPermanentDenied = activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } == true
            viewModel.onLocationPermissionStateChange(
                if (isPermanentDenied) PermissionState.PermanentlyDenied else PermissionState.Denied
            )
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::updateCameraPermissionState
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::updateLocationPermissionState
    )

    LaunchedEffect(uiState.invioConfermato) {
        if (uiState.invioConfermato) {
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = uiState.titolo,
                onValueChange = viewModel::onTitoloChange,
                label = { Text("Titolo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.descrizione,
                onValueChange = viewModel::onDescrizioneChange,
                label = { Text("Descrizione") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = uiState.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.privacyConsentGiven,
                    onCheckedChange = viewModel::onPrivacyConsentChange
                )
                Text(uiState.privacyConsentText)
            }

            Button(
                onClick = {
                    if (hasPermission(context, Manifest.permission.CAMERA)) {
                        viewModel.onCameraPermissionStateChange(PermissionState.Granted)
                        photoLauncher.launch("image/*")
                    } else {
                        val showRationale = activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                it,
                                Manifest.permission.CAMERA
                            )
                        } == true
                        if (showRationale) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Permesso fotocamera richiesto per allegare immagini alla segnalazione")
                            }
                        }
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Text("Seleziona foto")
            }

            when (uiState.cameraPermissionState) {
                PermissionState.Denied -> Text("Permesso fotocamera negato. Serve per allegare immagini.")
                PermissionState.PermanentlyDenied -> {
                    Text("Permesso fotocamera negato in modo permanente.")
                    Button(onClick = { openAppSettings(context) }) {
                        Text("Apri impostazioni")
                    }
                }
                else -> Unit
            }

            Button(
                onClick = {
                    if (!uiState.privacyConsentGiven) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Devi accettare il consenso privacy prima di usare la posizione")
                        }
                        return@Button
                    }

                    val hasLocationPermission = hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    if (hasLocationPermission) {
                        viewModel.onLocationPermissionStateChange(PermissionState.Granted)
                        fetchCurrentLocation(
                            context = context,
                            onLocationReceived = { location ->
                                viewModel.onPosizioneChange("Lat:${location.latitude}, Lng:${location.longitude}")
                            },
                            onLocationError = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Impossibile ottenere la posizione corrente")
                                }
                            }
                        )
                    } else {
                        val showRationale = activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } == true
                        if (showRationale) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("La posizione serve per geolocalizzare con precisione la segnalazione")
                            }
                        }
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            ) {
                Text("Ottieni posizione")
            }

            when (uiState.locationPermissionState) {
                PermissionState.Denied -> Text("Permesso posizione negato. Senza posizione non possiamo localizzare la segnalazione.")
                PermissionState.PermanentlyDenied -> {
                    Text("Permesso posizione negato in modo permanente.")
                    Button(onClick = { openAppSettings(context) }) {
                        Text("Apri impostazioni")
                    }
                }
                else -> Unit
            }

            uiState.posizione?.let { posizione ->
                Text("Posizione rilevata: $posizione")
            }

            Button(onClick = { viewModel.inviaSegnalazione() }) {
                Text("Invia")
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
    val locationTask = fusedLocationClient.lastLocation
    locationTask
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

private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
