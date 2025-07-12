package com.example.Store.data.repository

import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.model.UserRole
import com.example.Store.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val users = mutableMapOf(
        "admin@store.com" to Pair("admin123", UserRole.ADMIN),
        "user@store.com" to Pair("user123", UserRole.USER)
    )

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        delay(1000) // Simula tiempo de red

        val stored = users[email]
        return when {
            stored == null -> Result.failure(Exception("Usuario no encontrado"))
            stored.first != password -> Result.failure(Exception("Contraseña incorrecta"))
            else -> Result.success(LoginResult(stored.second))
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<Unit> {
        delay(500) // Simula procesamiento

        return if (users.containsKey(email)) {
            Result.failure(Exception("El usuario ya está registrado"))
        } else {
            users[email] = Pair(password, role)
            Result.success(Unit)
        }
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        delay(500) // Simula procesamiento

        return if (users.containsKey(email)) {
            println("🔐 Enviando enlace de recuperación a $email")
            Result.success(Unit)
        } else {
            Result.failure(Exception("Correo electrónico no encontrado"))
        }
    }
}