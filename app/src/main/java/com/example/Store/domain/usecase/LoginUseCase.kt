package com.example.Store.domain.usecase

import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<LoginResult> {
        return repository.login(email, password)
    }
}