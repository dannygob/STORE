package com.example.store.domain.usecase

import com.example.store.data.repository.UserRepository
import com.example.store.domain.model.LoginResult
import com.example.store.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkChecker: UserRepository,
) {

    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<LoginResult> {
        if (networkChecker.isConnected()) {
            val onlineResult = authRepository.login(email, password)
            return if (onlineResult.isSuccess) {
                onlineResult
            } else {
                // If online login fails, try offline login as a fallback
                val offlineResult = authRepository.offlineLogin(email)
                if (offlineResult.isSuccess) {
                    offlineResult
                } else {
                    // If both fail, return the online error, or offline if online wasn't attempted
                    onlineResult
                }
            }
        } else {
            // No internet connection, try offline login directly
            return authRepository.offlineLogin(email)
        }
    }
}

