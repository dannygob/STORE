package com.example.Store.data.repository

import android.util.Log
import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.model.UserRole
import com.example.Store.domain.repository.AuthRepository
import com.example.Store.util.NetworkChecker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkChecker: NetworkChecker,
) : AuthRepository {

    private val users = mutableMapOf(
        "admin@store.com" to Pair("admin123", UserRole.ADMIN),
        "user@store.com" to Pair("user123", UserRole.USER)
    )

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        delay(1000)

        return if (networkChecker.isConnected()) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                authResult.user ?: throw Exception("Usuario no encontrado en Firebase")
                val isAdmin = email.contains("admin", ignoreCase = true)
                val role = if (isAdmin) UserRole.ADMIN else UserRole.USER
                Result.success(LoginResult(role))
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            val stored = users[email]
            when {
                stored == null -> Result.failure(Exception("Usuario no encontrado (sin conexión)"))
                stored.first != password -> Result.failure(Exception("Contraseña incorrecta"))
                else -> Result.success(LoginResult(stored.second))
            }
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<Unit> {
        delay(500)

        return if (networkChecker.isConnected()) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            if (users.containsKey(email)) {
                Result.failure(Exception("El usuario ya está registrado (sin conexión)"))
            } else {
                users[email] = Pair(password, role)
                Result.success(Unit)
            }
        }
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        delay(500)

        return if (networkChecker.isConnected()) {
            try {
                auth.sendPasswordResetEmail(email).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            if (users.containsKey(email)) {
                Log.d(
                    "AuthRepository",
                    "🔐 Enviando recuperación a $email (modo sin conexión simulado)"
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Correo electrónico no encontrado"))
            }
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
