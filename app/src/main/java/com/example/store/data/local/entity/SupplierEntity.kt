package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val contactPerson: String?,
    val email: String?,
    val phone: String?
    // Consider adding address fields if necessary in the future
)
