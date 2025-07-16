package com.example.store.domain.repository

import com.example.store.domain.model.LoginResult
// UserRole removed from register method, but keeping import if LoginResult still uses it.
import com.example.store.domain.model.UserRole
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    // role: UserRole removed as Firebase Auth doesn't handle it directly.
    // This should be handled by a separate call to save user details (e.g., to Firestore).
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun recoverPassword(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    fun getAuthStateFlow(): Flow<FirebaseUser?>
    fun getCurrentUser(): FirebaseUser?
}