package com.example.store.domain.repository

import com.example.store.domain.model.LoginResult
import com.example.store.domain.model.UserRole
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    suspend fun register(email: String, password: String, role: UserRole): Result<LoginResult>
    suspend fun recoverPassword(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    fun getAuthState(): Flow<FirebaseUser?>
    suspend fun offlineLogin(email: String): Result<LoginResult>
}
