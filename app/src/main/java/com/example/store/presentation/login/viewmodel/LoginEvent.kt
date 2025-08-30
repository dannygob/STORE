package com.example.store.presentation.login.viewmodel

import com.example.store.domain.model.UserRole

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object Submit : LoginEvent()
    data class Register(val email: String, val password: String, val role: UserRole) : LoginEvent()
    data class RecoverPassword(val email: String) : LoginEvent()
}

/*- Use one of the registered users in your AuthRepositoryImpl. For example:
- User: admin@store.com
- Password: admin123
- o
- User: user@store.com
- Password: user123
- Make sure the user's role is ADMIN or USER as appropriate.*/