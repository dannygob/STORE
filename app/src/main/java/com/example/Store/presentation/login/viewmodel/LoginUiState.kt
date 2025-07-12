package com.example.Store.presentation.login.viewmodel

import com.example.Store.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val role: UserRole? = null,
)