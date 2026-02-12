package it.quartierevivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import it.quartierevivo.ui.theme.QuartiereVivoTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val segnalazioneViewModel: SegnalazioneViewModel by viewModels()
    private val mappaSegnalazioniViewModel: MappaSegnalazioniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mappaSegnalazioniViewModel.aggiornaSegnalazioni(
            listOf(
                Segnalazione("1", "Guasto illuminazione", 45.4641, 9.1916, null, "Manutenzione"),
                Segnalazione("2", "Rifiuti abbandonati", 45.4650, 9.1890, null, "Altro"),
                Segnalazione("3", "Area pericolosa", 45.4630, 9.1880, null, "Sicurezza")
            )
        )

        setContent {
            QuartiereVivoTheme {
                Surface {
                    val authState by viewModel.authState.collectAsState()
                    val authUiState by authViewModel.uiState.collectAsState()
                    var authScreen by rememberSaveable { mutableStateOf(AuthScreen.Login) }

                    when (authState) {
                        AuthState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        AuthState.Authenticated -> {
                            MappaSegnalazioniScreen(
                                viewModel = mappaSegnalazioniViewModel,
                                onLogoutClick = viewModel::signOut
                            )
                        }

                        AuthState.Unauthenticated -> {
                            when (authScreen) {
                                AuthScreen.Login -> LoginScreen(
                                    uiState = authUiState,
                                    onEmailChange = authViewModel::onEmailChanged,
                                    onPasswordChange = authViewModel::onPasswordChanged,
                                    onLoginClick = authViewModel::login,
                                    onNavigateToRegister = { authScreen = AuthScreen.Register },
                                    onNavigateToForgotPassword = { authScreen = AuthScreen.ForgotPassword }
                                )

                                AuthScreen.Register -> RegisterScreen(
                                    uiState = authUiState,
                                    onEmailChange = authViewModel::onEmailChanged,
                                    onPasswordChange = authViewModel::onPasswordChanged,
                                    onConfirmPasswordChange = authViewModel::onConfirmPasswordChanged,
                                    onRegisterClick = authViewModel::register,
                                    onNavigateToLogin = { authScreen = AuthScreen.Login }
                                )

                                AuthScreen.ForgotPassword -> ForgotPasswordScreen(
                                    uiState = authUiState,
                                    onEmailChange = authViewModel::onEmailChanged,
                                    onResetPasswordClick = authViewModel::sendPasswordReset,
                                    onNavigateToLogin = { authScreen = AuthScreen.Login }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class AuthScreen {
    Login,
    Register,
    ForgotPassword
}
