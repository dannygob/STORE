package com.example.store.presentation.purchases.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class PurchaseItemUi(
    val id: String = UUID.randomUUID().toString(),
    val productName: String,
    val supplierName: String?, // Optional: A purchase might not always have a supplier if it's a misc expense recorded here
    val quantity: Int,
    val unitPrice: Double,
    val purchaseDate: Long = System.currentTimeMillis() // Store as timestamp
) {
    val totalPrice: Double
        get() = quantity * unitPrice

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(purchaseDate))
    }

    fun getFormattedTotalPrice(): String {
        return String.format("$%.2f", totalPrice)
    }

    fun getFormattedUnitPrice(): String {
        return String.format("$%.2f", unitPrice)
    }
}
