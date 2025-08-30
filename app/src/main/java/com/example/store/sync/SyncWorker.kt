package com.example.store.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.store.data.local.AppDatabase
import com.example.store.data.local.entity.UserEntity
import com.example.store.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val db = AppDatabase.Companion.getDatabase(applicationContext)
        val userDao = db.userDao()

        try {
            // 1. Sincronizar usuarios registrados offline a Firebase
            val unsyncedUsers = userDao.getUnsyncedUsers()
            Log.d("SyncWorker", "Found ${unsyncedUsers.size} unsynced users.")

            for (userEntity in unsyncedUsers) {
                try {
                    // Check if user already exists in Firebase Auth (e.g., if they registered online on another device)
                    val firebaseUser = auth.fetchSignInMethodsForEmail(userEntity.email)
                        .await().signInMethods?.firstOrNull()?.let {
                        auth.signInWithEmailAndPassword(userEntity.email, userEntity.passwordHash)
                            .await().user
                    }

                    val uidToUse: String
                    if (firebaseUser != null) {
                        uidToUse = firebaseUser.uid
                        Log.d(
                            "SyncWorker",
                            "User ${userEntity.email} already exists in Firebase Auth. Using existing UID: $uidToUse"
                        )
                    } else {
                        // Register user in Firebase Auth
                        val authResult = auth.createUserWithEmailAndPassword(
                            userEntity.email,
                            userEntity.passwordHash
                        ).await()
                        uidToUse = authResult.user?.uid
                            ?: throw Exception("Firebase Auth user creation failed for ${userEntity.email}")
                        Log.d(
                            "SyncWorker",
                            "User ${userEntity.email} registered in Firebase Auth with UID: $uidToUse"
                        )
                    }

                    // Save user role to Firestore
                    firestore.collection("users").document(uidToUse)
                        .set(mapOf("role" to userEntity.role)).await()
                    Log.d("SyncWorker", "User role for ${userEntity.email} saved to Firestore.")

                    // Update local Room entry: set needsSync to false and update UID if it was temporary
                    val updatedUserEntity = userEntity.copy(uid = uidToUse, needsSync = false)
                    userDao.insertUser(updatedUserEntity)
                    Log.d("SyncWorker", "User ${userEntity.email} marked as synced in Room.")

                } catch (e: Exception) {
                    Log.e(
                        "SyncWorker",
                        "Failed to sync offline registered user ${userEntity.email}: ${e.message}",
                        e
                    )
                    // Continue to next user, retry this one later
                }
            }

            // 2. Descargar y sincronizar usuarios de Firebase a Room (existing functionality)
            val snapshot = firestore.collection("users").get().await()
            val firebaseUsers = snapshot.documents.mapNotNull { doc ->
                val uid = doc.id
                val email = doc.getString("email") ?: return@mapNotNull null
                val role = doc.getString("role") ?: UserRole.USER.name
                UserEntity(
                    uid,
                    email,
                    role = role,
                    needsSync = false
                ) // Assume downloaded users are synced
            }

            userDao.insertUsers(firebaseUsers)
            Log.d(
                "SyncWorker",
                "Usuarios descargados y sincronizados desde Firebase: ${firebaseUsers.size}"
            )

            return Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error during synchronization process", e)
            return Result.retry()
        }
    }
}
