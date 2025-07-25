package com.example.store.domain.model

data class ProductLocation(
    val productLocationId: String,
    val productId: String,
    val locationId: String,
    val quantity: Int,
    val aisle: String?,
    val shelf: String?,
    val level: String?
)
