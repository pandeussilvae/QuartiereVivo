package it.quartierevivo.data.remote

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    fun currentUserId(): String? = auth.currentUser?.uid
}
