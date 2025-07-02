package com.example.store.domain.usecase

import com.example.store.domain.model.LoginResult
import com.example.store.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<LoginResult> {
        return repository.login(email, password)
    }
}