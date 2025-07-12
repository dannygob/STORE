package com.example.Store.domain.repository


import com.example.Store.domain.model.LoginResult
import com.example.Store.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    private val firebaseAuth: FirebaseAuth = Firebase.auth

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return Result.failure(Exception("No user returned"))

            val snapshot = firestore.collection("users").document(user.uid).get().await()
            val roleString = snapshot.getString("role") ?: "USER"
            val role = UserRole.valueOf(roleString)

            Result.success(LoginResult(role = role))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<Unit> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return Result.failure(Exception("No user returned"))

            val userDoc = mapOf("email" to email, "role" to role.name)
            firestore.collection("users").document(user.uid).set(userDoc).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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
