package com.example.Store.data.repository

import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.ProductLocationEntity
import com.example.Store.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreService {

    override fun syncProduct(product: ProductEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection("products").document(product.productId).set(product).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getProduct(productId: String): Flow<Resource<ProductEntity?>> = callbackFlow {
        val subscription = firestore.collection("products").document(productId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Resource.Error(e.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(Resource.Success(snapshot.toObject(ProductEntity::class.java)))
                } else {
                    trySend(Resource.Success(null))
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun syncLocation(location: LocationEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection("locations").document(location.locationId).set(location).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun syncProductLocation(productLocation: ProductLocationEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection("product_locations").document(productLocation.productLocationId).set(productLocation).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun listenForLocationChanges(): Flow<Resource<List<LocationEntity>>> = callbackFlow {
        val subscription = firestore.collection("locations")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Resource.Error(e.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val locations = snapshot.toObjects(LocationEntity::class.java)
                    trySend(Resource.Success(locations))
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun listenForProductLocationChanges(): Flow<Resource<List<ProductLocationEntity>>> = callbackFlow {
        val subscription = firestore.collection("product_locations")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Resource.Error(e.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val productLocations = snapshot.toObjects(ProductLocationEntity::class.java)
                    trySend(Resource.Success(productLocations))
                }
            }
        awaitClose { subscription.remove() }
    }
}
