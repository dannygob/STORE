package com.example.Store.data.repository

import android.util.Log
import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.model.UserRole
import com.example.Store.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User not found in Firebase")

            val document = firestore.collection("users").document(user.uid).get().await()
            val roleString = document.getString("role") ?: UserRole.USER.name
            val role = UserRole.valueOf(roleString)

            Result.success(LoginResult(role))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed", e)
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User not created in Firebase")

            firestore.collection("users").document(user.uid).set(mapOf("role" to role.name)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration failed", e)
            Result.failure(e)
        }
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Password recovery failed", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser).isSuccess
        }
        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }.catch { e ->
        Log.e("AuthRepository", "Error in getAuthState Flow: ${e.message}")
        emit(null)
    }
}
