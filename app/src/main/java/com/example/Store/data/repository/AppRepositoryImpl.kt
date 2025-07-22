package com.example.Store.data.repository

import com.example.Store.data.local.dao.*
import com.example.Store.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class AppRepositoryImpl(
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val supplierDao: SupplierDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val warehouseDao: WarehouseDao,
    private val locationDao: LocationDao,
    private val productLocationDao: ProductLocationDao,
    private val preferenceDao: PreferenceDao,
    private val firestoreService: FirestoreService, // si usas Firestore
    private val externalScope: CoroutineScope,
) : AppRepository {

    // Product methods
    override fun getAllProducts(): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        try {
            productDao.getAllProducts().collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getProductById(productId: String): Flow<Resource<ProductEntity?>> = flow {
        emit(Resource.Loading())
        try {
            productDao.getProductById(productId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun searchProductsByName(query: String): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        try {
            productDao.searchProductsByName(query).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertProduct(product: ProductEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productDao.insert(product)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertAllProducts(products: List<ProductEntity>): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productDao.insertAll(products)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun updateProduct(product: ProductEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productDao.update(product)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteProduct(product: ProductEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productDao.delete(product)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteAllProducts(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productDao.deleteAllProducts()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    // Customer methods
    override fun getAllCustomers(): Flow<Resource<List<CustomerEntity>>> = flow {
        emit(Resource.Loading())
        try {
            customerDao.getAllCustomers().collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getCustomerById(customerId: String): Flow<Resource<CustomerEntity?>> = flow {
        emit(Resource.Loading())
        try {
            customerDao.getCustomerById(customerId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getCustomerByEmail(email: String): Flow<Resource<CustomerEntity?>> = flow {
        emit(Resource.Loading())
        try {
            customerDao.getCustomerByEmail(email).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertCustomer(customer: CustomerEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            customerDao.insert(customer)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun updateCustomer(customer: CustomerEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            customerDao.update(customer)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteCustomer(customer: CustomerEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            customerDao.delete(customer)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteAllCustomers(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            customerDao.deleteAllCustomers()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    // Supplier methods
    override fun getAllSuppliers(): Flow<Resource<List<SupplierEntity>>> = flow {
        emit(Resource.Loading())
        try {
            supplierDao.getAllSuppliers().collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getSupplierById(supplierId: String): Flow<Resource<SupplierEntity?>> = flow {
        emit(Resource.Loading())
        try {
            supplierDao.getSupplierById(supplierId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertSupplier(supplier: SupplierEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            supplierDao.insert(supplier)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun updateSupplier(supplier: SupplierEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            supplierDao.update(supplier)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteSupplier(supplier: SupplierEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            supplierDao.delete(supplier)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteAllSuppliers(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            supplierDao.deleteAllSuppliers()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    // Order methods
    override fun getAllOrders(): Flow<Resource<List<OrderEntity>>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.getAllOrders().collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getOrderById(orderId: String): Flow<Resource<OrderEntity?>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.getOrderById(orderId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getOrdersByCustomerId(customerId: String): Flow<Resource<List<OrderEntity>>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.getOrdersByCustomerId(customerId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<Resource<List<OrderEntity>>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.getOrdersByDateRange(startDate, endDate).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertOrder(order: OrderEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.insertOrder(order)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun updateOrder(order: OrderEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.updateOrder(order)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteOrder(order: OrderEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            orderDao.deleteOrder(order)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    // OrderItem methods
    override fun getOrderItemsForOrder(orderId: String): Flow<Resource<List<OrderItemEntity>>> = flow {
        emit(Resource.Loading())
        try {
            orderItemDao.getItemsForOrder(orderId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertOrderItem(orderItem: OrderItemEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            orderItemDao.insertOrderItem(orderItem)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertAllOrderItems(orderItems: List<OrderItemEntity>): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            orderItemDao.insertAll(orderItems)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun updateOrderItem(orderItem: OrderItemEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            orderItemDao.updateOrderItem(orderItem)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteOrderItem(orderItem: OrderItemEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            orderItemDao.deleteOrderItem(orderItem)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteAllOrderItemsForOrder(orderId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            orderItemDao.deleteAllForOrder(orderId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?> =
        orderItemDao.getOrderWithItems(orderId)

    override fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>> =
        orderItemDao.getAllOrdersWithItems()

    override suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        orderDao.insertOrderWithItems(order, items)
    }

    // Preferences
    override fun getPreference(key: String): Flow<String?> = preferenceDao.get(key)
    override suspend fun savePreference(key: String, value: String) =
        preferenceDao.save(UserPreferenceEntity(key, value))

    override suspend fun deletePreference(key: String) = preferenceDao.delete(key)
    override suspend fun deleteAllPreferences() = preferenceDao.clearAll()

    // Warehouse
    override fun getAllWarehouses(): Flow<Resource<List<WarehouseEntity>>> = flow {
        emit(Resource.Loading())
        try {
            warehouseDao.getAllWarehouses().collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getWarehouseById(warehouseId: String): Flow<Resource<WarehouseEntity?>> = flow {
        emit(Resource.Loading())
        try {
            warehouseDao.getWarehouseById(warehouseId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertWarehouse(warehouse: WarehouseEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            warehouseDao.insertWarehouse(warehouse)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun updateWarehouse(warehouse: WarehouseEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            warehouseDao.updateWarehouse(warehouse)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteWarehouse(warehouse: WarehouseEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            warehouseDao.deleteWarehouse(warehouse)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteAllWarehouses(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            warehouseDao.deleteAll()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    // Location
    override fun getAllLocations(): Flow<Resource<List<LocationEntity>>> = flow {
        emit(Resource.Loading())
        try {
            locationDao.getAllLocations().collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getLocationById(locationId: String): Flow<Resource<LocationEntity?>> = flow {
        emit(Resource.Loading())
        try {
            locationDao.getLocationById(locationId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertLocation(location: LocationEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            locationDao.insertLocation(location)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun updateLocation(location: LocationEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            locationDao.updateLocation(location)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteLocation(location: LocationEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            locationDao.deleteLocation(location)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteAllLocations(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            locationDao.deleteAll()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    // ProductLocation (Inventory)
    override fun getLocationsForProduct(productId: String): Flow<Resource<List<ProductLocationEntity>>> = flow {
        emit(Resource.Loading())
        try {
            productLocationDao.getLocationsForProduct(productId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getProductsAtLocation(locationId: String): Flow<Resource<List<ProductLocationEntity>>> = flow {
        emit(Resource.Loading())
        try {
            productLocationDao.getProductsAtLocation(locationId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun getTotalStockForProduct(productId: String): Flow<Resource<Int?>> = flow {
        emit(Resource.Loading())
        try {
            productLocationDao.getTotalStockForProduct(productId).collect { emit(Resource.Success(it)) }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun insertProductLocation(productLocation: ProductLocationEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productLocationDao.insertProductLocation(productLocation)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun updateProductLocation(productLocation: ProductLocationEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productLocationDao.updateProductLocation(productLocation)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteProductLocation(productLocation: ProductLocationEntity): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productLocationDao.deleteProductLocation(productLocation)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun deleteAllProductLocations(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            productLocationDao.deleteAll()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun addStockToLocation(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?,
        amount: Int,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val existing =
                productLocationDao.findProductLocation(productId, locationId, aisle, shelf, level)
            if (existing != null) {
                val updatedQuantity = (existing.quantity ?: 0) + amount
                productLocationDao.updateQuantityForProductLocation(
                    existing.productLocationId,
                    updatedQuantity
                )
            } else {
                val newLocation = ProductLocationEntity(
                    productLocationId = "${productId}_${locationId}_${aisle}_${shelf}_${level}",
                    productId = productId,
                    locationId = locationId,
                    aisle = aisle,
                    shelf = shelf,
                    level = level,
                    quantity = amount
                )
                productLocationDao.insertProductLocation(newLocation)
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    override fun transferStock(
        productId: String,
        fromLocationId: String,
        fromAisle: String?,
        fromShelf: String?,
        fromLevel: String?,
        toLocationId: String,
        toAisle: String?,
        toShelf: String?,
        toLevel: String?,
        amount: Int,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            addStockToLocation(productId, toLocationId, toAisle, toShelf, toLevel, amount)
            addStockToLocation(productId, fromLocationId, fromAisle, fromShelf, fromLevel, -amount)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido"))
        }
    }

    // Firestore sync
    override suspend fun syncProductToFirestore(product: ProductEntity): Result<Unit> =
        firestoreService.syncProduct(product)

    override fun getProductFromFirestore(productId: String): Flow<Result<ProductEntity?>> =
        firestoreService.getProduct(productId)

    override suspend fun syncLocationToFirestore(location: LocationEntity): Result<Unit> =
        firestoreService.syncLocation(location)

    override suspend fun syncProductLocationToFirestore(productLocation: ProductLocationEntity): Result<Unit> =
        firestoreService.syncProductLocation(productLocation)

    override fun listenForLocationChanges() {
        externalScope.launch {
            firestoreService.listenForLocationChanges().collect { result ->
                if (result is Resource.Success) {
                    result.data?.let { locationDao.insertAll(it) }
                }
            }
        }
    }

    override fun listenForProductLocationChanges() {
        externalScope.launch {
            firestoreService.listenForProductLocationChanges().collect { result ->
                if (result is Resource.Success) {
                    result.data?.let { productLocationDao.insertAll(it) }
                }
            }
        }
    }
}
