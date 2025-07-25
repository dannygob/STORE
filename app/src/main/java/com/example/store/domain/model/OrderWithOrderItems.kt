package com.example.store.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity

data class OrderWithOrderItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>,
)
