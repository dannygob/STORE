package com.example.store.presentation.sales.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// A simplified representation of an item within a sale for the summary
data class SoldItemSimple(
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val totalPrice: Double get() = quantity * unitPrice
}

data class SaleItemUi(
    val id: String = UUID.randomUUID().toString(),
    val transactionId: String = "SALE-${UUID.randomUUID().toString().take(8).uppercase()}",
    // For simplicity in this phase, itemsSold can be a summary string or a short list.
    // In a real app, this would be a list of SoldItem objects or references.
    val itemsSoldSummary: String, // e.g., "Apples (2), Bananas (1)" or just "Multiple Items"
    val items: List<SoldItemSimple> = emptyList(), // More detailed list for potential expansion
    val totalAmount: Double,
    val saleDate: Long = System.currentTimeMillis(),
    val customerName: String? = null // Optional
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(saleDate))
    }

    fun getFormattedTotalAmount(): String {
        return String.format(Locale.US, "$%.2f", totalAmount)
    }
}
