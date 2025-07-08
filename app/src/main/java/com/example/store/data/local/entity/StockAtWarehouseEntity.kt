package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "stock_at_warehouse",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE // If a product is deleted, its stock records are also deleted.
        ),
        ForeignKey(
            entity = WarehouseEntity::class,
            parentColumns = ["warehouseId"],
            childColumns = ["warehouseId"],
            onDelete = ForeignKey.CASCADE // If a warehouse is deleted, its stock records are also deleted.
        )
    ],
    // Unique constraint on (productId, warehouseId) to ensure one quantity entry per product per warehouse.
    indices = [Index(value = ["productId", "warehouseId"], unique = true), Index(value = ["warehouseId"])]
)
data class StockAtWarehouseEntity(
    @PrimaryKey val stockId: String = UUID.randomUUID().toString(),
    val productId: String,
    val warehouseId: String,
    val quantity: Int
)
