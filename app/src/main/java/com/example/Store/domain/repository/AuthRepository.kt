package com.example.Store.domain.repository

import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.model.UserRole
import com.example.Store.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<LoginResult>>
    fun register(email: String, password: String, role: UserRole): Flow<Resource<Unit>>
    fun recoverPassword(email: String): Flow<Resource<Unit>>
    suspend fun signOut(): Result<Unit>
    fun getAuthState(): Flow<FirebaseUser?>
}
