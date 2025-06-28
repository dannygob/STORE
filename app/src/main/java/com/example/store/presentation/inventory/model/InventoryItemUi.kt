package com.example.store.presentation.inventory.model

import java.util.UUID

data class InventoryItemUi(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int,
    val price: Double,
    val category: String? = null // Optional: for future use
) {
    // Helper to format price, can be expanded
    fun getFormattedPrice(): String {
        return String.format("$%.2f", price)
    }
}
