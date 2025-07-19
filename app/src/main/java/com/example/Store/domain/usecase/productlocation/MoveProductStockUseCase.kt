package com.example.Store.domain.usecase.productlocation

import com.example.Store.data.repository.AppRepository
import javax.inject.Inject

class MoveProductStockUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(
        productId: String,
        fromLocationId: String, fromAisle: String?, fromShelf: String?, fromLevel: String?,
        toLocationId: String, toAisle: String?, toShelf: String?, toLevel: String?,
        amount: Int
    ) {
        appRepository.transferStock(
            productId = productId,
            fromLocationId = fromLocationId,
            fromAisle = fromAisle,
            fromShelf = fromShelf,
            fromLevel = fromLevel,
            toLocationId = toLocationId,
            toAisle = toAisle,
            toShelf = toShelf,
            toLevel = toLevel,
            amount = amount
        )
    }
}