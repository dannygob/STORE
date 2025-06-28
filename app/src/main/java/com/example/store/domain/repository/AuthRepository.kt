package com.example.store.domain.repository

import com.example.store.domain.model.LoginResult
import com.example.store.domain.model.UserRole

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    suspend fun register(email: String, password: String, role: UserRole): Result<Unit>
    suspend fun recoverPassword(email: String): Result<Unit>
}