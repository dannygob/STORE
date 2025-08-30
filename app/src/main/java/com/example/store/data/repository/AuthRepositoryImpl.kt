package com.example.store.data.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val networkChecker: NetworkChecker,
    @ApplicationContext private val context: Context,
) : AuthRepository {

    init {
        scheduleHourlySync()
        // Call the function to create the initial admin user
        // This should ideally be called only once, e.g., on first app launch
        // For demonstration purposes, we'll call it here.
        // In a real app, you'd have a more robust mechanism to ensure it runs once.
        // For now, we'll make it a suspend function and call it from a coroutine scope
        // or ensure it's handled in a way that doesn't block the main thread.
        // For simplicity, we'll add it as a suspend function and assume it's called appropriately.
    }

    // Function to create an initial admin user for offline access
    suspend fun createInitialAdminUser() {
        val adminEmail = "admin@store.com"
        val adminPassword = "admin123" // This password will be hashed
        val adminRole = UserRole.ADMIN

        // Check if the admin user already exists to prevent duplicates
        val existingAdmin = userDao.getUserByEmail(adminEmail)
        if (existingAdmin == null) {
            val tempUid = UUID.randomUUID().toString() // Temporary UID for offline user
            val adminUserEntity = UserEntity(
                uid = tempUid,
                email = adminEmail,
                passwordHash = PasswordHasher.hash(adminPassword),
                role = adminRole.name,
                needsSync = true // Mark as needing sync to Firebase later
            )
            userDao.insertUser(adminUserEntity)
            Log.d(
                "AuthRepository",
                "Initial admin user 'admin@store.com' registered in Room for offline use."
            )
        } else {
            Log.d("AuthRepository", "Initial admin user 'admin@store.com' already exists in Room.")
        }
    }

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return if (networkChecker.isConnected()) {
            loginWithFirebase(email, password)
        } else {
            loginWithRoom(email, password)
        }
    }

    private suspend fun loginWithFirebase(email: String, password: String): Result<LoginResult> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User not found in Firebase")

            val document = firestore.collection("users").document(user.uid).get().await()
            val roleString = document.getString("role") ?: UserRole.USER.roleName
            val role = UserRole.fromString(roleString)

            val userEntity = UserEntity(
                uid = user.uid,
                email = email,
                passwordHash = PasswordHasher.hash(password),
                role = role.name,
                needsSync = false
            )
            userDao.insertUser(userEntity)
            scheduleSync()
            Result.success(LoginResult(role))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase login failed", e)
            Result.failure(e)
        }
    }

    private suspend fun loginWithRoom(email: String, password: String): Result<LoginResult> {
        return try {
            val localUser = userDao.getUserByEmail(email)
            if (localUser != null && localUser.passwordHash == PasswordHasher.hash(password)) {
                Result.success(LoginResult(UserRole.fromString(localUser.role)))
            } else if (localUser != null) {
                Result.failure(Exception("Invalid password."))
            } else {
                Result.failure(Exception("User not found locally."))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Room login failed", e)
            Result.failure(e)
        }
    }


    override suspend fun register(
        email: String,
        password: String,
        role: UserRole,
    ): Result<Unit> {
        return try {
            Log.d("AuthRepository", "Attempting to register user: $email with role: $role")

            if (networkChecker.isConnected()) {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("User not created in Firebase")
                Log.d("AuthRepository", "Firebase user created: ${user.uid}")

                firestore.collection("users").document(user.uid).set(mapOf("role" to role.roleName))
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

                Result.success(Unit)
            } else {
                val tempUid = UUID.randomUUID().toString()
                val userEntity = UserEntity(
                    uid = tempUid,
                    email = email,
                    passwordHash = PasswordHasher.hash(password),
                    role = role.name,
                    needsSync = true
                )
                userDao.insertUser(userEntity)
                Log.d(
                    "AuthRepository",
                    "User saved to Room (offline registration) with needsSync: ${userEntity.uid}."
                )
                scheduleSync()
                Result.success(Unit)
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
                Result.success(LoginResult(UserRole.fromString(localUser.role)))
            } else {
                Result.failure(Exception("No se encontró el usuario localmente. Inicia sesión con conexión a internet al menos una vez."))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Offline login failed", e)
            Result.failure(e)
        }
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }

    private fun scheduleHourlySync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}
