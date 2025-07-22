package com.example.Store.domain.usecase

import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.repository.AuthRepository
import com.example.Store.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(email: String, password: String): Flow<Resource<LoginResult>> {
        return repository.login(email, password)
    }
}