package com.example.store.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.store.data.local.AppDatabase
import com.example.store.data.local.entity.UserEntity
import com.example.store.domain.model.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val db = AppDatabase.Companion.getDatabase(applicationContext)
        val userDao = db.userDao()

        try {
            // 1. Sincronizar usuarios registrados offline a Firebase
            val unsyncedUsers = userDao.getUnsyncedUsers()
            Log.d("SyncWorker", "Found ${unsyncedUsers.size} unsynced users.")

            for (userEntity in unsyncedUsers) {
                try {
                    // For offline registered users, we only sync their role and email to Firestore.
                    // Firebase Auth user creation with password is not possible without plain text password.
                    // The user will need to use password recovery or register online to set their Firebase Auth password.

                    // Check if a user with this email already exists in Firestore (from another device or previous sync)
                    val firestoreUserDoc =
                        firestore.collection("users").whereEqualTo("email", userEntity.email).get()
                            .await().documents.firstOrNull()

                    val uidToUse: String
                    if (firestoreUserDoc != null) {
                        uidToUse = firestoreUserDoc.id
                        Log.d(
                            "SyncWorker",
                            "User ${userEntity.email} already exists in Firestore. Using existing UID: $uidToUse"
                        )
                    } else {
                        // Generate a new UID for Firestore if not already present
                        uidToUse = UUID.randomUUID().toString()
                        Log.d(
                            "SyncWorker",
                            "Creating new Firestore entry for ${userEntity.email} with UID: $uidToUse"
                        )
                    }

                    // Save user email and role to Firestore
                    firestore.collection("users").document(uidToUse)
                        .set(mapOf("email" to userEntity.email, "role" to userEntity.role)).await()
                    Log.d(
                        "SyncWorker",
                        "User email and role for ${userEntity.email} saved to Firestore."
                    )

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
