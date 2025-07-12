package com.example.Store.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.SET_NULL // If customer is deleted, set customerId to null in order
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class OrderEntity(
    @PrimaryKey val orderId: String = UUID.randomUUID().toString(),
    val customerId: String?, // Nullable if customer can be anonymous or deleted
    val orderDate: Long = System.currentTimeMillis(),
    val status: String, // e.g., "Pending", "Processing", "Shipped", "Delivered", "Canceled"
    val totalAmount: Double
)
