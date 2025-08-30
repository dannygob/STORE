package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val passwordHash: String = "", // Nuevo: hash de la contraseña para login offline
    val role: String,
    val needsSync: Boolean = false, // Indica si el usuario necesita ser sincronizado con Firebase
)
