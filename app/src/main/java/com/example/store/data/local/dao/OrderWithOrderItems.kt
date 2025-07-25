package com.example.store.data.local.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity

// POKO for representing an Order with its associated OrderItems
data class OrderWithOrderItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)
