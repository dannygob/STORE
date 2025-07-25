package com.example.store.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithOrderItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>,
)
