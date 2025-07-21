package com.example.Store.data.repository

import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.ProductLocationEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreService {

    override suspend fun syncProduct(product: ProductEntity): Result<Unit> {
        return try {
            firestore.collection("products").document(product.productId).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProduct(productId: String): Flow<Result<ProductEntity?>> = callbackFlow {
        val subscription = firestore.collection("products").document(productId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Result.failure(e))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(Result.success(snapshot.toObject(ProductEntity::class.java)))
                } else {
                    trySend(Result.success(null))
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun syncLocation(location: LocationEntity): Result<Unit> {
        return try {
            firestore.collection("locations").document(location.locationId).set(location).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncProductLocation(productLocation: ProductLocationEntity): Result<Unit> {
        return try {
            firestore.collection("product_locations").document(productLocation.productLocationId).set(productLocation).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun listenForLocationChanges(): Flow<Result<List<LocationEntity>>> = callbackFlow {
        val subscription = firestore.collection("locations")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Result.failure(e))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val locations = snapshot.toObjects(LocationEntity::class.java)
                    trySend(Result.success(locations))
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun listenForProductLocationChanges(): Flow<Result<List<ProductLocationEntity>>> = callbackFlow {
        val subscription = firestore.collection("product_locations")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Result.failure(e))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val productLocations = snapshot.toObjects(ProductLocationEntity::class.java)
                    trySend(Result.success(productLocations))
                }
            }
        awaitClose { subscription.remove() }
    }
}
