package it.quartierevivo.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageService(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
) {
    suspend fun uploadSegnalazioneImage(segnalazioneId: String, localUri: Uri): String {
        val imageRef = storage.reference.child("segnalazioni/$segnalazioneId.jpg")
        imageRef.putFile(localUri).await()
        return imageRef.downloadUrl.await().toString()
    }
}
