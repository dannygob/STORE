package com.example.Store.data.repository

import com.example.Store.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor() : FirestoreService {
    override suspend fun syncProduct(product: ProductEntity): Result<Unit> {
        return Result.success(Unit)
    }

    override fun getProduct(productId: String): Flow<Result<ProductEntity?>> {
        return flowOf(Result.success(null))
    }
}
