package com.example.store.domain.usecase.productlocation

import com.example.store.data.repository.AppRepository
import com.example.store.domain.model.ProductLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductsAtLocationUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(locationId: String): Flow<List<ProductLocation>> {
        return appRepository.getProductsAtLocation(locationId).map { products ->
            products.map { it.toDomainModel() }
        }
    }
}