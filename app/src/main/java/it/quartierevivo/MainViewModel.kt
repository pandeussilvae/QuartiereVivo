package it.quartierevivo

import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    fun tracciaLogin() {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }
}
