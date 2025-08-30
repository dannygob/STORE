package com.example.store.data.repository

import android.util.Log
import com.example.store.data.local.dao.UserDao
import com.example.store.data.local.entity.UserEntity
import com.example.store.domain.model.LoginResult
import com.example.store.domain.model.UserRole
import com.example.store.domain.repository.AuthRepository
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
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao, // ✅ Añadido para guardar en Room
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User not found in Firebase")

            val document = firestore.collection("users").document(user.uid).get().await()
            val roleString = document.getString("role") ?: UserRole.USER.name
            val role = UserRole.valueOf(roleString)

            // ✅ Guardar en Room para acceso offline
            val userEntity = UserEntity(uid = user.uid, email = email, role = role)
            userDao.insertUser(userEntity)

            Result.success(LoginResult(role))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed", e)
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<Unit> {
        return try {
            Log.d("AuthRepository", "Attempting to register user: $email with role: $role")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User not created in Firebase")
            Log.d("AuthRepository", "Firebase user created: ${user.uid}")

            firestore.collection("users").document(user.uid).set(mapOf("role" to role.name)).await()
            Log.d("AuthRepository", "User role saved to Firestore for user: ${user.uid}")

            // ✅ Guardar en Room para acceso offline
            val userEntity = UserEntity(uid = user.uid, email = email, role = role)
            try {
                userDao.insertUser(userEntity)
                Log.d("AuthRepository", "User saved to Room for user: ${user.uid}")
            } catch (roomException: Exception) {
                Log.e(
                    "AuthRepository",
                    "Failed to save user to Room for user ${user.uid}: ${roomException.message}",
                    roomException
                )
                // Decide if this should be a fatal error or if registration can proceed without local save
                // For now, we'll let it proceed but log the error.
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration failed for user $email: ${e.message}", e)
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
