package com.example.store.domain.usecase.inventory

import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.data.repository.AppRepository
import com.example.store.domain.model.PickListItem
import com.example.store.domain.model.ProductLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GeneratePickListUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(orderId: String): Flow<List<PickListItem>> = flow {
        val orderWithItems = appRepository.getOrderWithOrderItems(orderId).first()
        if (orderWithItems == null) {
            emit(emptyList())
            return@flow
        }

        val pickListItems = orderWithItems.orderItems.map { orderItem ->
            val product = appRepository.getProductById(orderItem.productId).first()
            val locations = appRepository.getLocationsForProduct(orderItem.productId).first()
                .map { it.toDomainModel() }
            PickListItem(
                productName = product?.name ?: "Unknown Product",
                productId = orderItem.productId,
                quantityToPick = orderItem.quantity,
                availableLocations = locations
            )
        }
        emit(pickListItems)
    }
}

fun ProductLocationEntity.toDomainModel(): ProductLocation {
    return ProductLocation(
        productLocationId = this.productLocationId,
        productId = this.productId,
        locationId = this.locationId,
        quantity = this.quantity ?: 0,
        aisle = TODO(),
        shelf = TODO(),
        level = TODO()
    )
}
