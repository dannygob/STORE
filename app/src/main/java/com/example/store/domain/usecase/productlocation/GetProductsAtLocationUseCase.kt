package com.example.store.domain.usecase.productlocation

import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsAtLocationUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(locationId: String): Flow<List<ProductLocationEntity>> {
        return appRepository.getProductsAtLocation(locationId)
    }
}
