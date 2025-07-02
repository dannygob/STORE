package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "order_items",
    primaryKeys = ["orderItemId"], // Explicitly defined composite PK if needed, or single as here.
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE // If an order is deleted, its items are also deleted.
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL // If a product is deleted, keep the order item but nullify productId
                                         // Alternatively, could be RESTRICT or NO_ACTION depending on business rules.
        )
    ],
    indices = [Index(value = ["orderId"]), Index(value = ["productId"])]
)
data class OrderItemEntity(
    val orderItemId: String = UUID.randomUUID().toString(),
    val orderId: String,
    val productId: String?, // Nullable if product is deleted (due to SET_NULL)
    val quantity: Int,
    val pricePerUnit: Double // Price of the product at the time the order was placed
)
