package com.example.store.data.local.dao

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.store.data.local.entity.OrderEntity
import java.util.UUID

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class OrderItemEntity(
    @PrimaryKey val orderItemId: String = UUID.randomUUID().toString(),
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val price: Double,
)

