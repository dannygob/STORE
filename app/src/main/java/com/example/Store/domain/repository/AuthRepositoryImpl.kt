package com.example.Store.domain.repository

import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val firebaseAuth: FirebaseAuth = Firebase.auth

    // In-memory user store is no longer the primary source for login/register with Firebase Auth
    // private val users = mutableMapOf(
    //     "admin@store.com" to Pair("admin123", UserRole.ADMIN),
    //     "user@store.com" to Pair("user123", UserRole.USER)
    // )

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            // Login successful with Firebase Auth.
            // Now, how to get UserRole? Firebase Auth doesn't store custom roles directly.
            // This would typically involve fetching a user profile document from Firestore
            // where the role is stored, using authResult.user?.uid.
            // For this stage, we'll return a default/placeholder role or indicate it's not yet implemented.
            // val firebaseUser = authResult.user // Contains UID, email, etc.
            // TODO: Fetch role from Firestore based on firebaseUser.uid
            val placeholderRole = UserRole.USER // Defaulting to USER for now
            Result.success(LoginResult(role = placeholderRole))
        } catch (e: Exception) {
            Result.failure(e) // Forward Firebase Auth exceptions (e.g., user-not-found, wrong-password)
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // User created successfully in Firebase Auth.
            // TODO: Optionally, store user role (and other profile info) in Firestore here.
            // For now, role is not stored directly in Firebase Auth user object.
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e) // Forward Firebase Auth exceptions (e.g., email-already-in-use, weak-password)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Placeholder for in-memory users, will be removed or adapted when login is Firebase-based
    // private val users = mutableMapOf(
    //     "admin@store.com" to Pair("admin123", UserRole.ADMIN),
    //     "user@store.com" to Pair("user123", UserRole.USER)
    // )
    // This map is no longer needed as primary auth mechanism.

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAuthState(): Flow<FirebaseUser?> {
        return firebaseAuth.authStateChanges()
    }
}