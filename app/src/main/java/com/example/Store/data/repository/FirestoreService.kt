package com.example.Store.data.repository

import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.ProductLocationEntity
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    suspend fun syncProduct(product: ProductEntity): Result<Unit>
    fun getProduct(productId: String): Flow<Result<ProductEntity?>>
    suspend fun syncLocation(location: LocationEntity): Result<Unit>
    suspend fun syncProductLocation(productLocation: ProductLocationEntity): Result<Unit>
    fun listenForLocationChanges(): Flow<Result<List<LocationEntity>>>
    fun listenForProductLocationChanges(): Flow<Result<List<ProductLocationEntity>>>
}
