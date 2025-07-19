package it.quartierevivo

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class MappaSegnalazioniViewModel(application: Application) : AndroidViewModel(application) {

    var query by mutableStateOf("")
        private set

    var segnalazioni by mutableStateOf(listOf<Segnalazione>())
        private set

    val filteredSegnalazioni: List<Segnalazione>
        get() = segnalazioni.filter {
            it.titolo.contains(query, true) || it.categoria.contains(query, true)
        }

    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(application)

    fun updateQuery(value: String) { query = value }

    fun setSegnalazioni(list: List<Segnalazione>) { segnalazioni = list }

    fun setupGeofences(context: Context) {
        val geofences = segnalazioni.filter { !it.risolta }.map {
            Geofence.Builder()
                .setRequestId(it.titolo)
                .setCircularRegion(it.latitudine, it.longitudine, 100f)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }
        if (geofences.isEmpty()) return

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        val intent = Intent(context, GeofenceReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        geofencingClient.addGeofences(request, pendingIntent)
    }
}
