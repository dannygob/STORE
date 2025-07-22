package com.example.Store.data.repository

import android.util.Log
import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.model.UserRole
import com.example.Store.domain.repository.AuthRepository
import com.example.Store.util.NetworkChecker
import com.example.Store.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
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

    override fun login(email: String, password: String): Flow<Resource<LoginResult>> = flow {
        emit(Resource.Loading())
        delay(1000)

        if (networkChecker.isConnected()) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                authResult.user ?: throw Exception("Usuario no encontrado en Firebase")
                val isAdmin = email.contains("admin", ignoreCase = true)
                val role = if (isAdmin) UserRole.ADMIN else UserRole.USER
                emit(Resource.Success(LoginResult(role)))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error desconocido"))
            }
        } else {
            // Offline login
            val user = users[email]
            if (user != null && user.first == password) {
                emit(Resource.Success(LoginResult(user.second)))
            } else {
                emit(Resource.Error("Invalid credentials"))
            }
        }
    }

    override fun register(email: String, password: String, role: UserRole): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        delay(500)

        if (networkChecker.isConnected()) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error al registrar"))
            }
        } else {
            if (users.containsKey(email)) {
                emit(Resource.Error("El usuario ya está registrado (sin conexión)"))
            } else {
                users[email] = Pair(password, role)
                emit(Resource.Success(Unit))
            }
        }
    }

    override fun recoverPassword(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        delay(500)

        if (networkChecker.isConnected()) {
            try {
                auth.sendPasswordResetEmail(email).await()
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "No se pudo recuperar la contraseña"))
            }
        } else {
            if (users.containsKey(email)) {
                Log.d(
                    "AuthRepository",
                    "🔐 Enviando recuperación a $email (modo sin conexión simulado)"
                )
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Correo electrónico no encontrado"))
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
