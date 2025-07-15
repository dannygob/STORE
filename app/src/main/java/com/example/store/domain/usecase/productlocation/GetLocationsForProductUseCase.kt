package com.example.store.domain.usecase.productlocation

import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationsForProductUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(productId: String): Flow<List<ProductLocationEntity>> {
        return appRepository.getLocationsForProduct(productId)
    }
}
