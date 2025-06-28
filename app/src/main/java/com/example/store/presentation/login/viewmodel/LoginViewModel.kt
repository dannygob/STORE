package com.example.store.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(
                    email = event.email,
                    emailError = null,
                    errorMessage = null
                )
            }

            is LoginEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password,
                    passwordError = null,
                    errorMessage = null
                )
            }

            LoginEvent.Submit -> {
                submitLogin()
            }
        }
    }

    private fun submitLogin() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        val emailValid = email.contains("@")
        val passwordValid = password.length >= 6

        if (!emailValid || !passwordValid) {
            _uiState.value = _uiState.value.copy(
                emailError = if (!emailValid) "Correo inválido" else null,
                passwordError = if (!passwordValid) "Mínimo 6 caracteres" else null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = loginUseCase(email, password)
            _uiState.value = when {
                result.isSuccess -> _uiState.value.copy(isSuccess = true, isLoading = false)
                result.isFailure -> _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido",
                    isLoading = false
                )

                else -> _uiState.value.copy(isLoading = false)
            }
        }
    }
}
