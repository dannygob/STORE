package com.example.store.domain.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.store.data.repository.UserRepository
import com.example.store.domain.model.LoginResult
import com.example.store.domain.model.UserRole
import com.example.store.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(
        context: Context?,
        email: String,
        password: String,
    ): Result<LoginResult> {
        if (context == null) {
            return Result.failure(Exception("Contexto no disponible para verificar la conexión a internet."))
        }

        if (!isOnline(context)) {
            return Result.failure(Exception("No hay conexión a internet. Por favor, verifica tu conexión."))
        }

        val authResult = authRepository.login(email, password)

        return if (authResult.isSuccess) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val userId =
                firebaseUser?.uid ?: return Result.failure(Exception("Usuario no encontrado"))

            val userResult = userRepository.getUser(context, userId)
            if (userResult.isSuccess) {
                val user = userResult.getOrNull()
                Result.success(LoginResult(user?.role ?: UserRole.USER))
            } else {
                Result.failure(Exception("No se pudo obtener el rol del usuario"))
            }
        } else {
            authResult
        }
    }

    // ✅ Verifica si hay conexión a Internet
    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
