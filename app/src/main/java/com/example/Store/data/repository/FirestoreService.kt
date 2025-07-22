package com.example.Store.data.repository

import com.example.Store.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    suspend fun syncProduct(product: ProductEntity): Result<Unit>
    fun getProduct(productId: String): Flow<Result<ProductEntity?>>
}
