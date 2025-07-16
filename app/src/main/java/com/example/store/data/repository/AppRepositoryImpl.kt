package com.example.store.data.repository

import com.example.store.data.local.dao.CustomerDao
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.local.dao.OrderDao
import com.example.store.data.local.dao.OrderItemDao
import com.example.store.data.local.dao.OrderWithOrderItems
import com.example.store.data.local.dao.*
import com.example.store.data.local.entity.*
import com.example.store.data.local.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore // Firebase Firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose // New import

// In a real app, DAOs would likely be injected (e.g., using Hilt)
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val supplierDao: SupplierDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val userPreferenceDao: UserPreferenceDao,
    private val locationDao: LocationDao,
    private val productLocationDao: ProductLocationDao,
    private val firestore: FirebaseFirestore,
    private val applicationScope: CoroutineScope
) : AppRepository {

    // Product Methods
    override fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    override fun getProductById(productId: String): Flow<ProductEntity?> = productDao.getProductById(productId)
    override suspend fun insertProduct(product: ProductEntity) = productDao.insert(product)
    override suspend fun updateProduct(product: ProductEntity) = productDao.update(product)
    override suspend fun deleteProduct(product: ProductEntity) = productDao.delete(product)
    override suspend fun insertAllProducts(products: List<ProductEntity>) = productDao.insertAll(products)
    override suspend fun deleteAllProducts() = productDao.deleteAllProducts()
    override fun searchProductsByName(query: String): Flow<List<ProductEntity>> = productDao.searchProductsByName(query)

    // Customer Methods
    override fun getAllCustomers(): Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    override fun getCustomerById(customerId: String): Flow<CustomerEntity?> = customerDao.getCustomerById(customerId)
    override fun getCustomerByEmail(email: String): Flow<CustomerEntity?> = customerDao.getCustomerByEmail(email)
    override suspend fun insertCustomer(customer: CustomerEntity) = customerDao.insert(customer)
    override suspend fun updateCustomer(customer: CustomerEntity) = customerDao.update(customer)
    override suspend fun deleteCustomer(customer: CustomerEntity) = customerDao.delete(customer)
    override suspend fun deleteAllCustomers() = customerDao.deleteAllCustomers()

    // Supplier Methods
    override fun getAllSuppliers(): Flow<List<SupplierEntity>> = supplierDao.getAllSuppliers()
    override fun getSupplierById(supplierId: String): Flow<SupplierEntity?> = supplierDao.getSupplierById(supplierId)
    override suspend fun insertSupplier(supplier: SupplierEntity) = supplierDao.insert(supplier)
    override suspend fun updateSupplier(supplier: SupplierEntity) = supplierDao.update(supplier)
    override suspend fun deleteSupplier(supplier: SupplierEntity) = supplierDao.delete(supplier)
    override suspend fun deleteAllSuppliers() = supplierDao.deleteAllSuppliers()

    // Order Methods
    override fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    override fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)
    override fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByCustomerId(customerId)
    override fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<OrderEntity>> = orderDao.getOrdersByDateRange(startDate, endDate)
    override suspend fun insertOrder(order: OrderEntity) = orderDao.insertOrder(order)
    override suspend fun updateOrder(order: OrderEntity) = orderDao.updateOrder(order)
    override suspend fun deleteOrder(order: OrderEntity) = orderDao.deleteOrder(order)

    // OrderItem Methods
    override fun getOrderItemsForOrder(orderId: String): Flow<List<OrderItemEntity>> = orderItemDao.getOrderItemsForOrder(orderId)
    override suspend fun insertOrderItem(orderItem: OrderItemEntity) = orderItemDao.insertOrderItem(orderItem)
    override suspend fun insertAllOrderItems(orderItems: List<OrderItemEntity>) = orderItemDao.insertAllOrderItems(orderItems)
    override suspend fun updateOrderItem(orderItem: OrderItemEntity) = orderItemDao.updateOrderItem(orderItem)
    override suspend fun deleteOrderItem(orderItem: OrderItemEntity) = orderItemDao.deleteOrderItem(orderItem)
    override suspend fun deleteAllOrderItemsForOrder(orderId: String) = orderItemDao.deleteAllOrderItemsForOrder(orderId)

    // Combined Operations
    override fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?> = orderItemDao.getOrderWithOrderItems(orderId)
    override fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>> = orderItemDao.getAllOrdersWithOrderItems()

    override suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        // This should ideally be a transaction handled at the DAO level if possible,
        // or ensure DAOs handle conflicts appropriately.
        // For simplicity here, we call them sequentially.
        // A @Transaction method in a DAO would be better for atomicity.
        // orderDao.insertOrder(order)
        // orderItemDao.insertAllOrderItems(items.map { it.copy(orderId = order.orderId) }) // Ensure items have correct orderId
        appDatabase.runInTransaction {
            // It's generally better to access DAOs via appDatabase instance inside transaction
            // if they weren't already class members. Since they are, we can use them.
            orderDao.insertOrder(order)
            orderItemDao.insertAllOrderItems(items.map { it.copy(orderId = order.orderId) })
        }
    }

    // User Preference Methods
    override fun getPreference(key: String): Flow<String?> = userPreferenceDao.getPreferenceValue(key)

    override suspend fun savePreference(key: String, value: String) {
        userPreferenceDao.insertPreference(UserPreferenceEntity(key, value))
    }

    override suspend fun deletePreference(key: String) {
        userPreferenceDao.deletePreference(key)
    }

    override suspend fun deleteAllPreferences() {
        userPreferenceDao.deleteAllPreferences()
    }

    // Location Methods
    override fun getAllLocations(): Flow<List<LocationEntity>> = locationDao.getAllLocations()
    override fun getLocationById(locationId: String): Flow<LocationEntity?> = locationDao.getLocationById(locationId)
    override suspend fun insertLocation(location: LocationEntity) {
        locationDao.insertLocation(location)
        syncLocationToFirestore(location)
    }
    override suspend fun updateLocation(location: LocationEntity) {
        locationDao.updateLocation(location)
        syncLocationToFirestore(location)
    }
    override suspend fun deleteLocation(location: LocationEntity) {
        locationDao.deleteLocation(location)
        firestore.collection("locations").document(location.locationId).delete().await()
    }
    override suspend fun deleteAllLocations() = locationDao.deleteAllLocations() // Note: This only deletes locally. A batch delete would be needed for Firestore.

    override suspend fun syncLocationToFirestore(location: LocationEntity): Result<Unit> {
        return try {
            firestore.collection("locations").document(location.locationId)
                .set(location)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun listenForRemoteProductLocationChanges() {
        firestore.collection("product_locations")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                for (doc in snapshots!!.documentChanges) {
                    val productLocation = doc.document.toObject(ProductLocationEntity::class.java)
                    applicationScope.launch {
                        productLocationDao.insertProductLocation(productLocation)
                    }
                }
            }
    }

    override fun listenForRemoteLocationChanges() {
        firestore.collection("locations")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                for (doc in snapshots!!.documentChanges) {
                    val location = doc.document.toObject(LocationEntity::class.java)
                    applicationScope.launch {
                        locationDao.insertLocation(location) // Using insert with OnConflictStrategy.REPLACE
                    }
                }
            }
    }

    // ProductLocation Methods
    override fun getLocationsForProduct(productId: String): Flow<List<ProductLocationEntity>> =
        productLocationDao.getLocationsForProduct(productId)

    override fun getProductsAtLocation(locationId: String): Flow<List<ProductLocationEntity>> =
        productLocationDao.getProductsAtLocation(locationId)

    override fun getTotalStockForProduct(productId: String): Flow<Int?> =
        productLocationDao.getTotalStockForProduct(productId)

    override suspend fun addStockToLocation(productId: String, locationId: String, aisle: String?, shelf: String?, level: String?, amount: Int) =
        appDatabase.addStockToLocation(productId, locationId, aisle, shelf, level, amount)

    override suspend fun transferStock(productId: String, fromLocationId: String, fromAisle: String?, fromShelf: String?, fromLevel: String?, toLocationId: String, toAisle: String?, toShelf: String?, toLevel: String?, amount: Int) =
        appDatabase.transferStock(productId, fromLocationId, fromAisle, fromShelf, fromLevel, toLocationId, toAisle, toShelf, toLevel, amount)

    override suspend fun addStockToLocation(productId: String, locationId: String, aisle: String?, shelf: String?, level: String?, amount: Int) {
        appDatabase.addStockToLocation(productId, locationId, aisle, shelf, level, amount)
        // This is a simplified sync. A more robust implementation would get the updated entity
        // from the transaction and sync that single entity.
        // For now, we'll fetch all locations for the product and sync them.
        val locations = productLocationDao.getLocationsForProduct(productId).first()
        locations.forEach { syncProductLocationToFirestore(it) }
    }

    override suspend fun transferStock(productId: String, fromLocationId: String, fromAisle: String?, fromShelf: String?, fromLevel: String?, toLocationId: String, toAisle: String?, toShelf: String?, toLevel: String?, amount: Int) {
        appDatabase.transferStock(productId, fromLocationId, fromAisle, fromShelf, fromLevel, toLocationId, toAisle, toShelf, toLevel, amount)
        // Similar to addStock, syncing all locations for simplicity.
        val locations = productLocationDao.getLocationsForProduct(productId).first()
        locations.forEach { syncProductLocationToFirestore(it) }
    }

    override suspend fun insertProductLocation(productLocation: ProductLocationEntity) {
        productLocationDao.insertProductLocation(productLocation)
        syncProductLocationToFirestore(productLocation)
    }

    override suspend fun updateProductLocation(productLocation: ProductLocationEntity) {
        productLocationDao.updateProductLocation(productLocation)
        syncProductLocationToFirestore(productLocation)
    }

    override suspend fun deleteProductLocation(productLocation: ProductLocationEntity) {
        productLocationDao.deleteProductLocation(productLocation)
        firestore.collection("product_locations").document(productLocation.productLocationId).delete().await()
    }

    override suspend fun deleteAllProductLocations() = productLocationDao.deleteAll() // Local only

    override suspend fun syncProductLocationToFirestore(productLocation: ProductLocationEntity): Result<Unit> {
        return try {
            firestore.collection("product_locations").document(productLocation.productLocationId)
                .set(productLocation)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Firestore Sync Methods
    override suspend fun syncProductToFirestore(product: ProductEntity): Result<Unit> {
        return try {
            // Using product.id as the document ID in Firestore
            firestore.collection("products").document(product.id)
                .set(product) // Using the ProductEntity directly (ensure it's Firestore compatible)
                .await() // Suspends until the operation is complete
            Result.success(Unit)
        } catch (e: Exception) {
            // Log.e("AppRepositoryImpl", "Error syncing product to Firestore", e) // Proper logging
            Result.failure(e)
        }
    }

    override fun getProductFromFirestore(productId: String): Flow<Result<ProductEntity?>> = kotlinx.coroutines.flow.callbackFlow {
        val documentRef = firestore.collection("products").document(productId)
        val listenerRegistration = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                close(error) // Close the flow on error
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                try {
                    val product = snapshot.toObject(ProductEntity::class.java)
                    trySend(Result.success(product))
                } catch (e: Exception) {
                    trySend(Result.failure(e)) // Failure in converting snapshot
                }
            } else {
                trySend(Result.success(null)) // Document does not exist
            }
        }
        awaitClose { listenerRegistration.remove() } // Remove listener when flow is cancelled
    }
}
