package com.example.store.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.store.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE uid = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?  // Método para login offline

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Método para insertar múltiples usuarios
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    // Consulta para obtener usuarios que necesitan sincronización
    @Query("SELECT * FROM users WHERE needsSync = 1")
    suspend fun getUnsyncedUsers(): List<UserEntity>

    // Consulta para obtener todos los usuarios
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    // Método para actualizar el estado de sincronización de un usuario
    @Query("UPDATE users SET needsSync = 0, uid = :uid WHERE email = :email")
    suspend fun markUserAsSynced(email: String, uid: String)

    // Método para actualizar el rol de un usuario
    @Query("UPDATE users SET role = :role WHERE email = :email")
    suspend fun updateUserRole(email: String, role: String)
}
