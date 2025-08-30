package com.example.store.data.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.store.data.local.dao.UserDao
import com.example.store.data.local.entity.UserEntity
import com.example.store.domain.model.LoginResult
import com.example.store.domain.model.UserRole
import com.example.store.domain.repository.AuthRepository
import com.example.store.sync.SyncWorker
import com.example.store.util.NetworkChecker
import com.example.store.util.PasswordHasher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val networkChecker: NetworkChecker,
    @ApplicationContext private val context: Context,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User not found in Firebase")

            val document = firestore.collection("users").document(user.uid).get().await()
            val roleString = document.getString("role") ?: UserRole.USER.name
            val role = UserRole.valueOf(roleString)

            // ✅ Guardar en Room para acceso offline con contraseña hasheada
            val userEntity = UserEntity(
                uid = user.uid,
                email = email,
                passwordHash = PasswordHasher.hash(password),
                role = role.name,
                needsSync = false // Ensure online logins are marked as synced
            )
            userDao.insertUser(userEntity)

            Result.success(LoginResult(role))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed", e)
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        role: UserRole,
    ): Result<LoginResult> {
        return try {
            Log.d("AuthRepository", "Attempting to register user: $email with role: $role")

            if (networkChecker.isConnected()) {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("User not created in Firebase")
                Log.d("AuthRepository", "Firebase user created: ${user.uid}")

                firestore.collection("users").document(user.uid).set(mapOf("role" to role.name))
                    .await()
                Log.d("AuthRepository", "User role saved to Firestore for user: ${user.uid}")

                val userEntity = UserEntity(
                    uid = user.uid,
                    email = email,
                    passwordHash = PasswordHasher.hash(password),
                    role = role.name,
                    needsSync = false
                )
                userDao.insertUser(userEntity)
                Log.d("AuthRepository", "User saved to Room (online registration): ${user.uid}")

                // After successful online registration, perform login
                login(email, password)
            } else {
                // Offline registration: Save to Room with needsSync = true, but no password hash for Firebase Auth
                val tempUid = UUID.randomUUID().toString() // Generate a unique UID for offline
                val userEntity = UserEntity(
                    uid = tempUid,
                    email = email,
                    passwordHash = "", // Do not store password hash for Firebase Auth in offline registration
                    role = role.name,
                    needsSync = true
                )
                userDao.insertUser(userEntity)
                Log.d(
                    "AuthRepository",
                    "User saved to Room (offline registration) with needsSync: ${userEntity.uid}. Password not stored for Firebase Auth."
                )

                // Schedule SyncWorker to push this registration to Firebase later
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(context).enqueue(syncRequest)
                Log.d("AuthRepository", "SyncWorker scheduled for offline registration.")

                // For offline registration, we can't perform a Firebase Auth login.
                // Instead, we return a LoginResult based on the locally registered user.
                Result.success(LoginResult(role))
            }
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

    override suspend fun offlineLogin(email: String): Result<LoginResult> {
        return try {
            val localUser = userDao.getUserByEmail(email)
            if (localUser != null) {
                Result.success(LoginResult(UserRole.valueOf(localUser.role)))
            } else {
                Result.failure(Exception("No se encontró el usuario localmente. Inicia sesión con conexión a internet al menos una vez."))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Offline login failed", e)
            Result.failure(e)
        }
    }
}
