package com.example.store.presentation.login.viewmodel

import com.example.store.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false, // For login success
    val registrationSuccess: Boolean = false, // For registration success
    val role: UserRole? = null,
)
/*- Use one of the registered users in your AuthRepositoryImpl. For example:
- User: admin@store.com
- Password: admin123
- o
- User: user@store.com
- Password: user123
- Make sure the user's role is ADMIN or USER as appropriate.*/