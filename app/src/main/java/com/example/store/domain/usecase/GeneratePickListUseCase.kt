package com.example.store.domain.usecase

import com.example.store.data.repository.AppRepository
import com.example.store.domain.model.PickListItem
import com.example.store.domain.model.ProductLocation
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GeneratePickListUseCase @Inject constructor(
    private val repository: AppRepository,
) {
    suspend operator fun invoke(orderIds: List<String>): List<PickListItem> {
        val allItems = mutableMapOf<String, Int>()

        for (orderId in orderIds) {
            val orderWithItems = repository.getOrderWithOrderItems(orderId).first()
            orderWithItems?.items?.forEach { item ->
                val productId = item.productId
                if (productId != null) {
                    allItems[productId] = (allItems[productId] ?: 0) + item.quantity
                }
            }
        }

        return allItems.map { (productId, quantity) ->
            val product = repository.getProductById(productId).first()
            val locations = repository.getLocationsForProduct(productId).first()
            PickListItem(
                productName = product?.name ?: "Unknown",
                productId = productId,
                quantityToPick = quantity,
                availableLocations = locations.map {
                    ProductLocation(
                        productLocationId = it.productLocationId,
                        productId = it.productId,
                        locationId = it.locationId,
                        quantity = it.quantity ?: 0,
                        aisle = it.aisle,
                        shelf = it.shelf,
                        level = it.level
                    )
                }
            )
        }
    }
}
