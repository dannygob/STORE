package com.example.Store.data.repository

import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.ProductLocationEntity
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

    override suspend fun syncLocationToFirestore(location: LocationEntity): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun syncProductLocation(productLocation: ProductLocationEntity): Result<Unit> {
        return Result.success(Unit)
    }

    override fun listenForLocationChanges(): Flow<Result<List<LocationEntity>>> {
        return flowOf(Result.success(emptyList()))
    }

    override fun listenForProductLocationChanges(): Flow<Result<List<ProductLocationEntity>>> {
        return flowOf(Result.success(emptyList()))
    }
}
