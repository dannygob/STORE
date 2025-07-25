package com.example.store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_at_warehouse")
data class StockAtWarehouseEntity(
    @PrimaryKey
    @ColumnInfo(name = "stockId")
    val stockId: String,

    @ColumnInfo(name = "productId")
    val productId: String,

    @ColumnInfo(name = "warehouseId")
    val warehouseId: String,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "lastUpdated")
    val lastUpdated: Long = System.currentTimeMillis(),
)
