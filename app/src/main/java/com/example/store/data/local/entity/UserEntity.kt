package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.store.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val role: UserRole,
)