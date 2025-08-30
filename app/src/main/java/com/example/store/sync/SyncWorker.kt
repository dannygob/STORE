package com.example.store.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.store.data.local.AppDatabase
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.UserDao
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.UserEntity
import com.example.store.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val db = AppDatabase.getDatabase(applicationContext)
        val userDao = db.userDao()
        val productDao = db.productDao()

        return try {
            syncUsers(auth, firestore, userDao)
            syncProducts(firestore, productDao)
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error during synchronization process", e)
            Result.retry()
        }
    }

    private suspend fun syncUsers(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        userDao: UserDao,
    ) {
        val unsyncedUsers = userDao.getUnsyncedUsers()
        Log.d("SyncWorker", "Found ${unsyncedUsers.size} unsynced users.")

        if (unsyncedUsers.isNotEmpty()) {
            for (userEntity in unsyncedUsers) {
                var uidToUse: String? = null
                try {
                    val tempPassword = UUID.randomUUID().toString()
                    val authResult = auth.createUserWithEmailAndPassword(
                        userEntity.email,
                        tempPassword
                    ).await()
                    uidToUse = authResult.user?.uid
                    Log.d(
                        "SyncWorker",
                        "Created Firebase Auth user for ${userEntity.email} with UID: $uidToUse"
                    )

                    auth.sendPasswordResetEmail(userEntity.email).await()
                    Log.d("SyncWorker", "Sent password reset email to ${userEntity.email}")
                } catch (e: FirebaseAuthUserCollisionException) {
                    Log.d(
                        "SyncWorker",
                        "User ${userEntity.email} already exists in Firebase Auth. Attempting to get UID from Firestore."
                    )
                    val firestoreUserDoc = firestore.collection("users")
                        .whereEqualTo("email", userEntity.email)
                        .get().await().documents.firstOrNull()
                    uidToUse = firestoreUserDoc?.id
                    Log.d("SyncWorker", "UID from Firestore for ${userEntity.email}: $uidToUse")
                } catch (e: Exception) {
                    Log.e(
                        "SyncWorker",
                        "Failed to sync offline registered user ${userEntity.email}: ${e.message}",
                        e
                    )
                }

                if (uidToUse != null) {
                    try {
                        firestore.collection("users").document(uidToUse).set(
                            mapOf(
                                "email" to userEntity.email,
                                "role" to userEntity.role
                            )
                        ).await()
                        Log.d(
                            "SyncWorker",
                            "User email and role for ${userEntity.email} saved to Firestore."
                        )

                        val updatedUserEntity = userEntity.copy(uid = uidToUse, needsSync = false)
                        userDao.insertUser(updatedUserEntity)
                        Log.d("SyncWorker", "User ${userEntity.email} marked as synced in Room.")
                    } catch (e: Exception) {
                        Log.e(
                            "SyncWorker",
                            "Error updating Firestore or Room for ${userEntity.email}: ${e.message}",
                            e
                        )
                    }
                } else {
                    Log.w(
                        "SyncWorker",
                        "Could not obtain UID for ${userEntity.email}. Skipping sync for this user."
                    )
                }
            }
        } else {
            Log.d("SyncWorker", "No unsynced users found.")
        }

        try {
            val snapshot = firestore.collection("users").get().await()
            val firebaseUsers = snapshot.documents.mapNotNull { doc ->
                val uid = doc.id
                val email = doc.getString("email") ?: return@mapNotNull null
                val role = doc.getString("role") ?: UserRole.USER.name
                UserEntity(
                    uid = uid,
                    email = email,
                    role = role,
                    needsSync = false
                )
            }

            if (firebaseUsers.isNotEmpty()) {
                userDao.insertUsers(firebaseUsers)
                Log.d(
                    "SyncWorker",
                    "Usuarios descargados y sincronizados desde Firebase: ${firebaseUsers.size}"
                )
            } else {
                Log.d("SyncWorker", "No users found in Firebase to sync.")
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Failed to download users from Firestore: ${e.message}", e)
        }
    }

    private suspend fun syncProducts(
        firestore: FirebaseFirestore,
        productDao: ProductDao,
    ) {
        val unsyncedProducts = productDao.getUnsyncedProducts()
        Log.d("SyncWorker", "Found ${unsyncedProducts.size} unsynced products.")

        if (unsyncedProducts.isNotEmpty()) {
            for (productEntity in unsyncedProducts) {
                try {
                    firestore.collection("products").document(productEntity.id).set(productEntity)
                        .await()
                    productDao.insert(productEntity.copy(needsSync = false))
                    Log.d("SyncWorker", "Product ${productEntity.name} synced to Firestore.")
                } catch (e: Exception) {
                    Log.e(
                        "SyncWorker",
                        "Failed to sync product ${productEntity.name}: ${e.message}",
                        e
                    )
                }
            }
        } else {
            Log.d("SyncWorker", "No unsynced products found.")
        }

        try {
            val snapshot = firestore.collection("products").get().await()
            val firebaseProducts = snapshot.toObjects(ProductEntity::class.java)

            if (firebaseProducts.isNotEmpty()) {
                productDao.insertAll(firebaseProducts)
                Log.d(
                    "SyncWorker",
                    "Products downloaded and synced from Firestore: ${firebaseProducts.size}"
                )

                val productIds = firebaseProducts.map { it.id }
                productDao.updateSyncStatus(productIds)
            } else {
                Log.d("SyncWorker", "No products found in Firestore to sync.")
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Failed to download products from Firestore: ${e.message}", e)
        }
    }
}