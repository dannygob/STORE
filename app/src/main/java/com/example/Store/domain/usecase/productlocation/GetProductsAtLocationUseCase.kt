package com.example.Store.domain.usecase.productlocation

import com.example.Store.domain.model.ProductLocation
import com.example.Store.data.repository.AppRepository
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