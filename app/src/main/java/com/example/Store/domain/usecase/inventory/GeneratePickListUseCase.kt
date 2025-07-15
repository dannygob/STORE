package com.example.Store.domain.usecase.inventory


import com.example.Store.data.repository.AppRepository
import com.example.store.data.local.entity.ProductLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class PickListItem(
    val productName: String,
    val productId: String,
    val quantityToPick: Int,
    val availableLocations: List<ProductLocationEntity>
)

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