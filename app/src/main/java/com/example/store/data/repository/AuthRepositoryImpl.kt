package com.example.store.data.repository


import com.example.store.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    // Aquí podrías inyectar api, dao, dataStore, etc.
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(1000) // Simula tiempo de red

        return if (email == "usuario@ejemplo.com" && password == "123456") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Credenciales inválidas"))
        }
    }
}
