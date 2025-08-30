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

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val db = AppDatabase.Companion.getDatabase(applicationContext)
            val userDao = db.userDao()

            val snapshot = firestore.collection("users").get().await()
            val users = snapshot.documents.mapNotNull { doc ->
                val uid = doc.id
                val email = doc.getString("email") ?: return@mapNotNull null
                val role = doc.getString("role") ?: UserRole.USER.name
                UserEntity(uid, email, UserRole.valueOf(role.uppercase()))
            }

            userDao.insertUsers(users)
            Log.d("SyncWorker", "Usuarios sincronizados: ${users.size}")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error al sincronizar usuarios", e)
            Result.retry()
        }
    }
}