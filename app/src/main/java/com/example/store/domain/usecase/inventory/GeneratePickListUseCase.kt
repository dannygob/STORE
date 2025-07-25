package com.example.store.domain.usecase.inventory

import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.data.repository.AppRepository
import com.example.store.domain.model.PickListItem
import com.example.store.domain.model.ProductLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GeneratePickListUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(orderId: String): Flow<List<PickListItem>> {
        return appRepository.getOrderWithOrderItems(orderId).flatMapLatest { orderWithItems ->
            if (orderWithItems == null) {
                return@flatMapLatest flow { emit(emptyList()) }
            }

            val itemFlows = orderWithItems.items.map { orderItem ->
                val productFlow = appRepository.getProductById(orderItem.productId)
                val locationsFlow = appRepository.getLocationsForProduct(orderItem.productId)
                    .map { locations -> locations.map { it.toDomainModel() } }

                productFlow.combine(locationsFlow) { product, locations ->
                    PickListItem(
                        productName = product?.name ?: "Unknown Product",
                        productId = orderItem.productId,
                        quantityToPick = orderItem.quantity,
                        availableLocations = locations
                    )
                }
            }
            combine(itemFlows) { it.toList() }
        }
    }
}

fun ProductLocationEntity.toDomainModel(): ProductLocation {
    return ProductLocation(
        productLocationId = this.productLocationId,
        productId = this.productId,
        locationId = this.locationId,
        quantity = this.quantity ?: 0,
        aisle = this.aisle,
        shelf = this.shelf,
        level = this.level
    )
}
