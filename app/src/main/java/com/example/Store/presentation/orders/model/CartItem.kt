package com.example.Store.presentation.orders.model

import java.util.Locale

data class CartItem(
    val product: ProductUi,
    var quantity: Int,
) {
    fun getFormattedPrice(): String = String.format(Locale.US, "$%.2f", product.price * quantity)
}
