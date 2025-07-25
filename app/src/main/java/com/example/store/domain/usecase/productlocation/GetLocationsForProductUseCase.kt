package com.example.store.domain.usecase.productlocation

import com.example.store.data.repository.AppRepository
import com.example.store.domain.model.ProductLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationsForProductUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(productId: String): Flow<List<ProductLocation>> {
        return appRepository.getLocationsForProduct(productId).map { locations ->
            locations.map { it.toDomainModel() }
        }
    }
}