package com.example.Store.presentation.login.viewmodel

import com.example.Store.domain.model.UserRole

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object Submit : LoginEvent()
    data class Register(val email: String, val password: String, val role: UserRole) : LoginEvent()
    data class RecoverPassword(val email: String) : LoginEvent()
}