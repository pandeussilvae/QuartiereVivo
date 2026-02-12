package it.quartierevivo

import android.os.Build
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FcmTokenManager(
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    suspend fun registerCurrentToken() {
        val token = messaging.token.await()
        registerToken(token)
    }

    fun registerToken(token: String) {
        val payload = mapOf(
            "token" to token,
            "uid" to auth.currentUser?.uid,
            "platform" to "android",
            "deviceModel" to Build.MODEL,
            "updatedAt" to Timestamp.now(),
        )
        firestore.collection("fcmTokens").document(token).set(payload)
    }
}
