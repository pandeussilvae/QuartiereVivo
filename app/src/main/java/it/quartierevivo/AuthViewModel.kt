package it.quartierevivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null, infoMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null, infoMessage = null)
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = null, infoMessage = null)
    }

    fun login() {
        val state = _uiState.value
        if (!isValidEmail(state.email)) {
            _uiState.value = state.copy(errorMessage = "Inserisci un'email valida")
            return
        }
        if (state.password.length < 6) {
            _uiState.value = state.copy(errorMessage = "La password deve avere almeno 6 caratteri")
            return
        }

        executeAuthAction {
            authRepository.login(state.email.trim(), state.password)
        }
    }

    fun register() {
        val state = _uiState.value
        if (!isValidEmail(state.email)) {
            _uiState.value = state.copy(errorMessage = "Inserisci un'email valida")
            return
        }
        if (state.password.length < 6) {
            _uiState.value = state.copy(errorMessage = "La password deve avere almeno 6 caratteri")
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(errorMessage = "Le password non coincidono")
            return
        }

        executeAuthAction {
            authRepository.register(state.email.trim(), state.password)
        }
    }

    fun sendPasswordReset() {
        val state = _uiState.value
        if (!isValidEmail(state.email)) {
            _uiState.value = state.copy(errorMessage = "Inserisci un'email valida")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null, infoMessage = null)
            runCatching {
                authRepository.sendPasswordReset(state.email.trim())
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    infoMessage = "Email di reset inviata"
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = it.localizedMessage ?: "Errore durante l'invio email"
                )
            }
        }
    }

    private fun executeAuthAction(action: suspend () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null, infoMessage = null)
            runCatching { action() }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        password = "",
                        confirmPassword = ""
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = it.localizedMessage ?: "Operazione non riuscita"
                    )
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)
