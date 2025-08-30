package com.example.store.presentation.login.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.store.domain.model.UserRole
import com.example.store.presentation.login.viewmodel.LoginEvent
import com.example.store.presentation.login.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onAdminLogin: () -> Unit,
    onUserLogin: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            when (state.role) {
                UserRole.ADMIN -> onAdminLogin()
                UserRole.USER -> onUserLogin()
                else -> {
                    // Rol desconocido
                    viewModel.onLoginHandled()
                }
            }
        }
    }

    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            Toast.makeText(context, "Usuario registrado correctamente. Por favor, inicie sesión.", Toast.LENGTH_LONG).show()
            viewModel.onRegistrationHandled()
            // After successful registration, navigate back to login to allow the user to log in
            // This is handled by the onAdminLogin/onUserLogin callbacks in MainNavHost
            // which navigate to Dashboard. We don't need explicit navigation here.
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Login", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.emailError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                state.emailError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    isError = state.passwordError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                state.passwordError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onEvent(LoginEvent.Submit)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Login")
                }

                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onEvent(LoginEvent.RecoverPassword(state.email))
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot your password?")
                }

                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onEvent(
                            LoginEvent.Register(
                                state.email,
                                state.password,
                                UserRole.USER
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Register")
                }

                state.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
/*- Use one of the registered users in your AuthRepositoryImpl. For example:
- User: admin@store.com
- Password: admin123
- o
- User: user@store.com
- Password: user123
- Make sure the user's role is ADMIN or USER as appropriate.*/
