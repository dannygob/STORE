package com.example.store.data.local.entity

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
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class OrderEntity(
    @PrimaryKey val orderId: String = UUID.randomUUID().toString(),
    val customerId: String?, // Nullable because of SET_NULL
    val orderDate: Long = System.currentTimeMillis(),
    val status: String,
    val totalAmount: Double,
)