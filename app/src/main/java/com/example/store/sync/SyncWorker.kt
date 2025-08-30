package com.example.store.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.store.data.local.AppDatabase
import com.example.store.data.local.entity.UserEntity
import com.example.store.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.example.store.data.local.dao.ProductDao

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val db = AppDatabase.Companion.getDatabase(applicationContext)
        val userDao = db.userDao()
        val productDao = db.productDao()

        try {
            // 1. Sincronizar usuarios
            syncUsers(auth, firestore, userDao)

            // 2. Sincronizar productos
            syncProducts(firestore, productDao)

            syncUsers(auth, firestore, userDao)

            // 2. Sincronizar productos
            syncProducts(firestore, productDao)

            return Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error during synchronization process", e)
            return Result.retry()
        }
    }

    private suspend fun syncUsers(auth: FirebaseAuth, firestore: FirebaseFirestore, userDao: UserDao) {
        // 1. Sincronizar usuarios registrados offline a Firebase
        val unsyncedUsers = userDao.getUnsyncedUsers()
        Log.d("SyncWorker", "Found ${unsyncedUsers.size} unsynced users.")

        for (userEntity in unsyncedUsers) {
            var uidToUse: String? = null
            try {
                // Check if user already exists in Firebase Auth
                val signInMethods = auth.fetchSignInMethodsForEmail(userEntity.email).await().signInMethods
                if (signInMethods.isNullOrEmpty()) {
                    // User does not exist, create them
                    val tempPassword = UUID.randomUUID().toString()
                    val authResult = auth.createUserWithEmailAndPassword(userEntity.email, tempPassword).await()
                    uidToUse = authResult.user?.uid
                    Log.d("SyncWorker", "Created Firebase Auth user for ${userEntity.email} with UID: $uidToUse")

                    // Send password reset email
                    auth.sendPasswordResetEmail(userEntity.email).await()
                    Log.d("SyncWorker", "Sent password reset email to ${userEntity.email}")
                } else {
                    // User already exists, try to get UID from Firestore
                    val firestoreUserDoc = firestore.collection("users").whereEqualTo("email", userEntity.email).get().await().documents.firstOrNull()
                    uidToUse = firestoreUserDoc?.id
                    Log.d("SyncWorker", "User ${userEntity.email} already exists in Firebase Auth. UID from Firestore: $uidToUse")
                }

                if (uidToUse != null) {
                    // Save user email and role to Firestore
                    firestore.collection("users").document(uidToUse!!)
                        .set(mapOf("email" to userEntity.email, "role" to userEntity.role)).await()
                    Log.d("SyncWorker", "User email and role for ${userEntity.email} saved to Firestore.")

                    // Update local Room entry: set needsSync to false and update UID
                    val updatedUserEntity = userEntity.copy(uid = uidToUse!!, needsSync = false)
                    userDao.insertUser(updatedUserEntity)
                    Log.d("SyncWorker", "User ${userEntity.email} marked as synced in Room.")
                } else {
                    Log.w("SyncWorker", "Could not obtain UID for ${userEntity.email}. Skipping sync for this user.")
                }

            } catch (e: FirebaseAuthUserCollisionException) {
                Log.w("SyncWorker", "User ${userEntity.email} already exists, but was not detected. Skipping creation.", e)
            } catch (e: Exception) {
                Log.e("SyncWorker", "Failed to sync offline registered user ${userEntity.email}: ${e.message}", e)
                // Continue to next user, retry this one later
            }
        }

        // 2. Descargar y sincronizar usuarios de Firebase a Room
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
    }

    private suspend fun syncProducts(firestore: FirebaseFirestore, productDao: ProductDao) {
        // 1. Sincronizar productos locales a Firebase
        val unsyncedProducts = productDao.getUnsyncedProducts()
        Log.d("SyncWorker", "Found ${unsyncedProducts.size} unsynced products.")

        for (productEntity in unsyncedProducts) {
            try {
                firestore.collection("products").document(productEntity.id).set(productEntity).await()
                productDao.insert(productEntity.copy(needsSync = false))
                Log.d("SyncWorker", "Product ${productEntity.name} synced to Firestore.")
            } catch (e: Exception) {
                Log.e("SyncWorker", "Failed to sync product ${productEntity.name}: ${e.message}", e)
            }
        }

        // 2. Descargar y sincronizar productos de Firebase a Room
        val snapshot = firestore.collection("products").get().await()
        val firebaseProducts = snapshot.toObjects(com.example.store.data.local.entity.ProductEntity::class.java)
        productDao.insertAll(firebaseProducts)
        Log.d(
            "SyncWorker",
            "Products downloaded and synced from Firestore: ${firebaseProducts.size}"
        )
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
