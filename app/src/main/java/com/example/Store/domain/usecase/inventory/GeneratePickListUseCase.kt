package com.example.Store.domain.usecase.inventory

import com.example.Store.domain.model.PickListItem
import com.example.Store.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GeneratePickListUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(orderId: String): Flow<List<PickListItem>> = flow {
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