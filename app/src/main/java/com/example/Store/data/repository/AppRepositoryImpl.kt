package com.example.Store.data.repository

import com.example.Store.data.local.dao.*
import com.example.Store.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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
    override fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    override fun getProductById(productId: String): Flow<ProductEntity?> =
        productDao.getProductById(productId)

    override fun searchProductsByName(query: String): Flow<List<ProductEntity>> =
        productDao.searchProductsByName(query)

    override suspend fun insertProduct(product: ProductEntity) = productDao.insert(product)
    override suspend fun insertAllProducts(products: List<ProductEntity>) =
        productDao.insertAll(products)

    override suspend fun updateProduct(product: ProductEntity) = productDao.update(product)
    override suspend fun deleteProduct(product: ProductEntity) = productDao.delete(product)
    override suspend fun deleteAllProducts() = productDao.deleteAllProducts()

    // Customer methods
    override fun getAllCustomers(): Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    override fun getCustomerById(customerId: String): Flow<CustomerEntity?> =
        customerDao.getCustomerById(customerId)

    override fun getCustomerByEmail(email: String): Flow<CustomerEntity?> =
        customerDao.getCustomerByEmail(email)

    override suspend fun insertCustomer(customer: CustomerEntity) =
        customerDao.insert(customer)

    override suspend fun updateCustomer(customer: CustomerEntity) =
        customerDao.update(customer)

    override suspend fun deleteCustomer(customer: CustomerEntity) =
        customerDao.delete(customer)

    override suspend fun deleteAllCustomers() = customerDao.deleteAllCustomers()

    // Supplier methods
    override fun getAllSuppliers(): Flow<List<SupplierEntity>> = supplierDao.getAllSuppliers()
    override fun getSupplierById(supplierId: String): Flow<SupplierEntity?> =
        supplierDao.getSupplierById(supplierId)

    override suspend fun insertSupplier(supplier: SupplierEntity) =
        supplierDao.insert(supplier)

    override suspend fun updateSupplier(supplier: SupplierEntity) =
        supplierDao.update(supplier)

    override suspend fun deleteSupplier(supplier: SupplierEntity) =
        supplierDao.delete(supplier)

    override suspend fun deleteAllSuppliers() = supplierDao.deleteAllSuppliers()

    // Order methods
    override fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    override fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)
    override fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersByCustomerId(customerId)

    override fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<OrderEntity>> =
        orderDao.getOrdersByDateRange(startDate, endDate)

    override suspend fun insertOrder(order: OrderEntity) = orderDao.insertOrder(order)
    override suspend fun updateOrder(order: OrderEntity) = orderDao.updateOrder(order)
    override suspend fun deleteOrder(order: OrderEntity) = orderDao.deleteOrder(order)

    // OrderItem methods
    override fun getOrderItemsForOrder(orderId: String): Flow<List<OrderItemEntity>> =
        orderItemDao.getOrderItemsForOrder(orderId)

    override suspend fun insertOrderItem(orderItem: OrderItemEntity) =
        orderItemDao.insertOrderItem(orderItem)

    override suspend fun insertAllOrderItems(orderItems: List<OrderItemEntity>) =
        orderItemDao.insertAllOrderItems(orderItems)

    override suspend fun updateOrderItem(orderItem: OrderItemEntity) =
        orderItemDao.updateOrderItem(orderItem)

    override suspend fun deleteOrderItem(orderItem: OrderItemEntity) =
        orderItemDao.deleteOrderItem(orderItem)

    override suspend fun deleteAllOrderItemsForOrder(orderId: String) =
        orderItemDao.deleteAllOrderItemsForOrder(orderId)

    override fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?> =
        orderItemDao.getOrderWithOrderItems(orderId)

    override fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>> =
        orderItemDao.getAllOrdersWithOrderItems()

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
    override fun getAllWarehouses(): Flow<List<WarehouseEntity>> = warehouseDao.getAllWarehouses()
    override fun getWarehouseById(warehouseId: String): Flow<WarehouseEntity?> =
        warehouseDao.getWarehouseById(warehouseId)

    override suspend fun insertWarehouse(warehouse: WarehouseEntity) =
        warehouseDao.insertWarehouse(warehouse)

    override suspend fun updateWarehouse(warehouse: WarehouseEntity) =
        warehouseDao.updateWarehouse(warehouse)

    override suspend fun deleteWarehouse(warehouse: WarehouseEntity) =
        warehouseDao.deleteWarehouse(warehouse)

    override suspend fun deleteAllWarehouses() = warehouseDao.deleteAllWarehouses()

    // Location
    override fun getAllLocations(): Flow<List<LocationEntity>> = locationDao.getAllLocations()
    override fun getLocationById(locationId: String): Flow<LocationEntity?> =
        locationDao.getLocationById(locationId)

    override suspend fun insertLocation(location: LocationEntity) =
        locationDao.insertLocation(location)

    override suspend fun updateLocation(location: LocationEntity) =
        locationDao.updateLocation(location)

    override suspend fun deleteLocation(location: LocationEntity) =
        locationDao.deleteLocation(location)

    override suspend fun deleteAllLocations() = locationDao.deleteAll()

    // ProductLocation (Inventory)
    override fun getLocationsForProduct(productId: String): Flow<List<ProductLocationEntity>> =
        productLocationDao.getLocationsForProduct(productId)

    override fun getProductsAtLocation(locationId: String): Flow<List<ProductLocationEntity>> =
        productLocationDao.getProductsAtLocation(locationId)

    override fun getTotalStockForProduct(productId: String): Flow<Int?> =
        productLocationDao.getTotalStockForProduct(productId)

    override suspend fun insertProductLocation(productLocation: ProductLocationEntity) =
        productLocationDao.insertProductLocation(productLocation)

    override suspend fun updateProductLocation(productLocation: ProductLocationEntity) =
        productLocationDao.updateProductLocation(productLocation)

    override suspend fun deleteProductLocation(productLocation: ProductLocationEntity) =
        productLocationDao.deleteProductLocation(productLocation)

    override suspend fun deleteAllProductLocations() = productLocationDao.deleteAll()

    override suspend fun addStockToLocation(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?,
        amount: Int,
    ) {
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
    }

    override suspend fun transferStock(
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
    ) {
        addStockToLocation(productId, toLocationId, toAisle, toShelf, toLevel, amount)
        addStockToLocation(productId, fromLocationId, fromAisle, fromShelf, fromLevel, -amount)
    }

    // Firestore sync
    override suspend fun syncProductToFirestore(product: ProductEntity): Result<Unit> =
        firestoreService.syncProduct(product)

    override fun getProductFromFirestore(productId: String): Flow<Result<ProductEntity?>> =
        firestoreService.getProduct(productId)

    override suspend fun syncLocationToFirestore(location: LocationEntity): Result<Unit> =
        firestoreService.syncLocationToFirestore(location)

    override suspend fun syncProductLocationToFirestore(productLocation: ProductLocationEntity): Result<Unit> =
        firestoreService.syncProductLocation(productLocation)

    override fun listenForLocationChanges() {
        externalScope.launch {
            firestoreService.listenForLocationChanges().collect { result ->
                result.onSuccess { locations ->
                    locationDao.insertAll(locations)
                }
            }
        }
    }

    override fun listenForProductLocationChanges() {
        externalScope.launch {
            firestoreService.listenForProductLocationChanges().collect { result ->
                result.onSuccess { productLocations ->
                    productLocationDao.insertAll(productLocations)
                }
            }
        }
    }
}
