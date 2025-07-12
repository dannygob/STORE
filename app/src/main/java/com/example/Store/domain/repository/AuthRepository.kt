package com.example.Store.domain.repository

import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.model.UserRole
import com.google.firebase.auth.FirebaseUser // Moved import
import kotlinx.coroutines.flow.Flow // Moved import

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    suspend fun register(email: String, password: String, role: UserRole): Result<Unit>
    suspend fun recoverPassword(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    fun getAuthState(): Flow<FirebaseUser?>
}