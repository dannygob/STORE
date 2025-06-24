package com.example.store.presentation.orders.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELED;

    // Helper to get a user-friendly string
    fun getDisplayValue(): String {
        return this.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

data class OrderItemUi(
    val id: String = UUID.randomUUID().toString(),
    val orderNumber: String = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}",
    val customerName: String,
    val orderDate: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderStatus.PENDING,
    val totalAmount: Double,
    val itemSummary: String // e.g., "3 items: Apple, Banana, Milk"
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(orderDate))
    }

    fun getFormattedTotalAmount(): String {
        return String.format("$%.2f", totalAmount)
    }
}
