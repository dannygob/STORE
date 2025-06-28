package com.example.store.presentation.inventory.model

import java.time.LocalDate

data class InventoryItemUi(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val category: String,
    val expirationDate: LocalDate? = null,
    val imageUri: String? = null,
) {
    fun getFormattedPrice(): String = "$${String.format("%.2f", price)}"

    fun isLowStock(): Boolean = quantity in 1..5
    fun isOutOfStock(): Boolean = quantity == 0
    fun isExpiringSoon(): Boolean {
        return expirationDate?.let {
            val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), it)
            daysUntil in 0..3
        } == true
    }
}