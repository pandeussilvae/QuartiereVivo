package it.quartierevivo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Clustering
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import it.quartierevivo.ui.theme.VerdeOliva
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MappaSegnalazioniScreen(
    viewModel: MappaSegnalazioniViewModel = viewModel(),
    onOpenDetails: (String) -> Unit,
    onOpenPreferences: () -> Unit,
) {
    val context = LocalContext.current
    val segnalazioni by viewModel.segnalazioniFiltrate.collectAsState()

    val avgLat = segnalazioni.map { it.latitudine }.averageOrNull() ?: 0.0
    val avgLng = segnalazioni.map { it.longitudine }.averageOrNull() ?: 0.0
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            LatLng(avgLat, avgLng),
            13f,
        )
    }

    var expanded by remember { mutableStateOf(false) }
    val categorie = segnalazioni.map { it.categoria }.distinct().filter { it.isNotEmpty() }

    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
    }
    val properties = remember(myLocation) {
        MapProperties(isMyLocationEnabled = myLocation != null)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(SnackbarHostState()) },
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = properties,
            ) {
                Clustering(items = segnalazioni, clusterItemContent = { item ->
                    val icon = rememberImageBitmapDescriptor(item.immagineUrl)
                    MarkerInfoWindow(
                        state = rememberMarkerState(position = item.position),
                        icon = icon,
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text(text = item.titolo)
                            Text(text = "Stato: ${item.status}")
                            Spacer(Modifier.height(4.dp))
                            Button(onClick = { onOpenDetails(item.id) }) {
                                Text("Dettagli")
                            }
                        }
                    }
                })
            }

            Column(modifier = Modifier.align(Alignment.TopCenter).padding(8.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = viewModel.categoriaFiltro.collectAsState().value ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor(),
                    )
                    androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Tutte") }, onClick = {
                            viewModel.setCategoriaFiltro(null)
                            expanded = false
                        })
                        categorie.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = {
                                viewModel.setCategoriaFiltro(cat)
                                expanded = false
                            })
                        }
                    }
                }
            }

            Row(modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = VerdeOliva)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtri", tint = VerdeOliva)
                }
                IconButton(onClick = onOpenPreferences) {
                    Icon(Icons.Default.Notifications, contentDescription = "Preferenze notifiche", tint = VerdeOliva)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = VerdeOliva)
                }
            }

            FloatingActionButton(
                onClick = {
                    val fused = LocationServices.getFusedLocationProviderClient(context)
                    coroutineScope.launch {
                        try {
                            val loc = fused.lastLocation.await()
                            loc?.let { location ->
                                myLocation = LatLng(location.latitude, location.longitude)
                                cameraPositionState.position = cameraPositionState.position.copy(target = myLocation!!)
                            }
                        } catch (_: Exception) {
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp),
                containerColor = VerdeOliva,
                shape = CircleShape,
            ) {
                Icon(
                    painterResource(android.R.drawable.ic_menu_mylocation),
                    contentDescription = "My Location",
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
fun rememberImageBitmapDescriptor(url: String?): BitmapDescriptor? {
    val context = LocalContext.current
    var descriptor by remember(url) { mutableStateOf<BitmapDescriptor?>(null) }
    LaunchedEffect(url) {
        descriptor = url?.let {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context).data(it).build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = result.drawable.toBitmap(width = 64, height = 64)
                BitmapDescriptorFactory.fromBitmap(bitmap)
            } else {
                null
            }
        }
    }
    return descriptor
}

private fun List<Double>.averageOrNull(): Double? = if (isNotEmpty()) average() else null
