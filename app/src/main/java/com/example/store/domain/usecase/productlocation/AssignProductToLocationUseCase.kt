package com.example.store.domain.usecase.productlocation

import com.example.store.data.repository.AppRepository
import javax.inject.Inject


class AssignProductToLocationUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?,
        amount: Int
    ) {
        appRepository.addStockToLocation(productId, locationId, aisle, shelf, level, amount)
    }
}