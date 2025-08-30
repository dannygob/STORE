package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String, // ID único del usuario
    val email: String, // Correo electrónico del usuario
    val passwordHash: String = "", // Hash de la contraseña para login offline
    val role: String, // Rol del usuario (admin, user, etc.)
    val needsSync: Boolean = false, // Indica si el usuario necesita ser sincronizado con Firebase
    val lastSyncTimestamp: Long = 0L, // (Opcional) Marca de tiempo de la última sincronización
)
