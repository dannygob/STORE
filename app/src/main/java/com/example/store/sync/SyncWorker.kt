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
        val db = AppDatabase.Companion.getDatabase(applicationContext)
        val userDao = db.userDao()
        val productDao = db.productDao()

        try {
            // 1. Sincronizar usuarios
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

        if (unsyncedUsers.isNotEmpty()) {
            for (userEntity in unsyncedUsers) {
                var uidToUse: String?
                try {
                    // Usamos el método adecuado para obtener el estado de autenticación del usuario
                    val result = auth.fetchSignInMethodsForEmail(userEntity.email).await()
                    val signInMethods = result.signInMethods

                    if (signInMethods.isNullOrEmpty()) {
                        // El usuario no existe, crearlo
                        val tempPassword = UUID.randomUUID().toString()
                        val authResult =
                            auth.createUserWithEmailAndPassword(userEntity.email, tempPassword)
                                .await()
                        uidToUse = authResult.user?.uid
                        Log.d(
                            "SyncWorker",
                            "Created Firebase Auth user for ${userEntity.email} with UID: $uidToUse"
                        )

                        // Enviar correo para restablecer la contraseña
                        auth.sendPasswordResetEmail(userEntity.email).await()
                        Log.d("SyncWorker", "Sent password reset email to ${userEntity.email}")
                    } else {
                        // El usuario ya existe, intentar obtener el UID desde Firestore
                        val firestoreUserDoc =
                            firestore.collection("users").whereEqualTo("email", userEntity.email)
                                .get().await().documents.firstOrNull()
                        uidToUse = firestoreUserDoc?.id
                        Log.d(
                            "SyncWorker",
                            "User ${userEntity.email} already exists in Firebase Auth. UID from Firestore: $uidToUse"
                        )
                    }

                    if (uidToUse != null) {
                        // Guardar correo y rol del usuario en Firestore
                        firestore.collection("users").document(uidToUse)
                            .set(mapOf("email" to userEntity.email, "role" to userEntity.role))
                            .await()
                        Log.d(
                            "SyncWorker",
                            "User email and role for ${userEntity.email} saved to Firestore."
                        )

                        // Actualizar la entrada en Room: setear needsSync a false y actualizar UID
                        val updatedUserEntity = userEntity.copy(uid = uidToUse, needsSync = false)
                        userDao.insertUser(updatedUserEntity)
                        Log.d("SyncWorker", "User ${userEntity.email} marked as synced in Room.")
                    } else {
                        Log.w(
                            "SyncWorker",
                            "Could not obtain UID for ${userEntity.email}. Skipping sync for this user."
                        )
                    }

                } catch (e: Exception) {
                    Log.e(
                        "SyncWorker",
                        "Failed to sync offline registered user ${userEntity.email}: ${e.message}",
                        e
                    )
                    // Continuar con el siguiente usuario, reintentar este después
                }
            }
        } else {
            Log.d("SyncWorker", "No unsynced users found.")
        }

        // 2. Descargar y sincronizar usuarios de Firebase a Room
        try {
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
                ) // Asumimos que los usuarios descargados ya están sincronizados
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

    private suspend fun syncProducts(firestore: FirebaseFirestore, productDao: ProductDao) {
        // 1. Sincronizar productos locales a Firebase
        val unsyncedProducts = productDao.getUnsyncedProducts()
        Log.d("SyncWorker", "Found ${unsyncedProducts.size} unsynced products.")

        if (unsyncedProducts.isNotEmpty()) {
            for (productEntity in unsyncedProducts) {
                try {
                    firestore.collection("products").document(productEntity.id).set(productEntity)
                        .await()
                    productDao.insert(productEntity.copy(needsSync = false)) // Marcar como sincronizado
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

        // 2. Descargar y sincronizar productos de Firebase a Room
        try {
            val snapshot = firestore.collection("products").get().await()
            val firebaseProducts = snapshot.toObjects(ProductEntity::class.java)

            if (firebaseProducts.isNotEmpty()) {
                productDao.insertAll(firebaseProducts)
                Log.d(
                    "SyncWorker",
                    "Products downloaded and synced from Firestore: ${firebaseProducts.size}"
                )

                // Marcar productos sincronizados
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
