package com.example.store.data.repository

import UserDao
import android.content.Context
import android.util.Log

import com.example.store.data.local.entity.UserEntity
import com.example.store.domain.model.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
) {

    // Buscar usuario por userId (Firebase UID)
    suspend fun getUserById(context: Context?, userId: String): Result<UserEntity> {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            if (!snapshot.exists()) throw Exception("Documento no existe en Firestore")

            val email = snapshot.getString("email") ?: "unknown@example.com"
            val roleString = snapshot.getString("role") ?: UserRole.USER.name
            val role = UserRole.valueOf(roleString.uppercase())

            val user = UserEntity(uid = userId, email = email, role = role.name)
            userDao.insertUser(user) // Guardar localmente para acceso offline

            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al obtener desde Firestore, usando Room", e)

            val localUser = userDao.getUserById(userId)
            if (localUser != null) {
                Result.success(localUser)
            } else {
                Result.failure(Exception("No se pudo obtener el usuario ni online ni offline", e))
            }
        }
    }

    // Buscar usuario por email (offline)
    suspend fun getUserByEmail(context: Context?, email: String): Result<UserEntity> {
        return try {
            val localUser = userDao.getUserByEmail(email)
            if (localUser != null) {
                Result.success(localUser)
            } else {
                Result.failure(Exception("Usuario no encontrado offline"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error buscando usuario por email", e)
            Result.failure(e)
        }
    }
}
