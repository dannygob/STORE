package com.example.store.presentation.orders.model

import java.util.Locale
import java.util.UUID

data class ProductUi(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    var stock: Int, // Made var so it can be decremented/incremented
    val price: Double
) {
    fun getFormattedPrice(): String = String.format(Locale.US, "$%.2f", price)
}
