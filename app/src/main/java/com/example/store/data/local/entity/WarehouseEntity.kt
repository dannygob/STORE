package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "warehouses")
data class WarehouseEntity(
    @PrimaryKey val warehouseId: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String?,
    val capacity: Double? // e.g., cubic meters, pallet count, or other relevant measure
)
