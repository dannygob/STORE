package com.example.store.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.domain.model.UserRole
import com.example.store.domain.repository.AuthRepository
import com.example.store.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.email,
                        emailError = null,
                        errorMessage = null
                    )
                }
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.password,
                        passwordError = null,
                        errorMessage = null
                    )
                }
            }
            LoginEvent.Submit -> {
                onLoginClicked()
            }

            is LoginEvent.Register -> {
                onRegisterClicked(event.email, event.password, event.role)
            }

            is LoginEvent.RecoverPassword -> {
                onRecoverPassword(event.email)
            }
        }
    }

    private fun onLoginClicked() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        val emailValid = email.contains("@")
        val passwordValid = password.length >= 6

        if (!emailValid || !passwordValid) {
            _uiState.update {
                it.copy(
                    emailError = if (!emailValid) "Correo inválido" else null,
                    passwordError = if (!passwordValid) "Mínimo 6 caracteres" else null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = loginUseCase(email, password)
            if (result.isSuccess) {
                val loginResult = result.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        role = loginResult?.role
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    private fun onRegisterClicked(email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(email, password, role)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, errorMessage = "Usuario registrado correctamente.")
                } else {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al registrar"
                    )
                }
            }
        }
    }

    private fun onRecoverPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.recoverPassword(email)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, errorMessage = "Se envió un correo de recuperación.")
                } else {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "No se pudo recuperar contraseña"
                    )
                }
            }
        }
    }

    fun onLoginHandled() {
        _uiState.update { it.copy(isSuccess = false, errorMessage = null) }
    }
}