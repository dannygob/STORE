package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val category: String?,
    val price: Double,
    val stockQuantity: Int,
    val imageUrl: String?,
    val supplierId: String? // Can be null if supplier is not tracked or product is internally sourced
)
