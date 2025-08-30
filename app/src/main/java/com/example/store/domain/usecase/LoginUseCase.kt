package com.example.store.domain.usecase

import com.example.store.domain.model.LoginResult
import com.example.store.domain.repository.AuthRepository
import com.example.store.util.NetworkChecker // Correct import for NetworkChecker
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkChecker: NetworkChecker, // Corrected type to NetworkChecker
) {

    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<LoginResult> {
        return if (networkChecker.isConnected()) {
            // If online, attempt login via Firebase
            authRepository.login(email, password)
        } else {
            // If offline, attempt login via Room
            authRepository.offlineLogin(email)
        }
    }
}
