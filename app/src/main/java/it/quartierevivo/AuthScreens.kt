package it.quartierevivo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import it.quartierevivo.ui.theme.VerdeOliva

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    AuthFormContainer(title = "Accedi") {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        AuthMessages(uiState)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLoginClick,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login")
            }
        }
        TextButton(onClick = onNavigateToForgotPassword) {
            Text("Password dimenticata?", color = VerdeOliva)
        }
        TextButton(onClick = onNavigateToRegister) {
            Text("Non hai un account? Registrati", color = VerdeOliva)
        }
    }
}

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    AuthFormContainer(title = "Registrazione") {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Conferma password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        AuthMessages(uiState)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRegisterClick,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Crea account")
            }
        }
        TextButton(onClick = onNavigateToLogin) {
            Text("Hai già un account? Accedi", color = VerdeOliva)
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onResetPasswordClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    AuthFormContainer(title = "Recupera password") {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        AuthMessages(uiState)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onResetPasswordClick,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Invia email di reset")
            }
        }
        TextButton(onClick = onNavigateToLogin) {
            Text("Torna al login", color = VerdeOliva)
        }
    }
}

@Composable
private fun AuthFormContainer(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        content()
    }
}

@Composable
private fun AuthMessages(uiState: AuthUiState) {
    uiState.errorMessage?.let {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = it, color = MaterialTheme.colorScheme.error)
    }
    uiState.infoMessage?.let {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = it, color = VerdeOliva)
    }
}
