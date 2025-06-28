package com.example.store.domain.repository

import com.example.store.domain.model.LoginResult
import com.example.store.domain.model.UserRole
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val users = mutableMapOf(
        "admin@store.com" to Pair("admin123", UserRole.ADMIN),
        "user@store.com" to Pair("user123", UserRole.USER)
    )

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        val stored = users[email]
        return when {
            stored == null -> Result.failure(Exception("Usuario no encontrado"))
            stored.first != password -> Result.failure(Exception("Contraseña incorrecta"))
            else -> Result.success(LoginResult(stored.second))
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<Unit> {
        return if (users.containsKey(email)) {
            Result.failure(Exception("El usuario ya existe"))
        } else {
            users[email] = Pair(password, role)
            Result.success(Unit)
        }
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return if (users.containsKey(email)) {
            // Simulamos envío de correo
            println("🔐 Enviar link de recuperación a $email")
            Result.success(Unit)
        } else {
            Result.failure(Exception("Correo no registrado"))
        }
    }
}