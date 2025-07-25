package com.example.store.domain.model

data class PickListItem(
    val productName: String,
    val productId: String,
    val quantityToPick: Int,
    val availableLocations: List<ProductLocation>
)
