package com.example.Store.data.repository

import com.example.Store.data.local.entity.*

import kotlinx.coroutines.flow.Flow

interface AppRepository {

    // Product operations
    fun getAllProducts(): Flow<List<ProductEntity>>
    fun getProductById(productId: String): Flow<ProductEntity?>
    fun searchProductsByName(query: String): Flow<List<ProductEntity>>
    suspend fun insertProduct(product: ProductEntity)
    suspend fun insertAllProducts(products: List<ProductEntity>)
    suspend fun updateProduct(product: ProductEntity)
    suspend fun deleteProduct(product: ProductEntity)
    suspend fun deleteAllProducts()

    // Customer operations
    fun getAllCustomers(): Flow<List<CustomerEntity>>
    fun getCustomerById(customerId: String): Flow<CustomerEntity?>
    fun getCustomerByEmail(email: String): Flow<CustomerEntity?>
    suspend fun insertCustomer(customer: CustomerEntity)
    suspend fun updateCustomer(customer: CustomerEntity)
    suspend fun deleteCustomer(customer: CustomerEntity)
    suspend fun deleteAllCustomers()

    // Supplier operations
    fun getAllSuppliers(): Flow<List<SupplierEntity>>
    fun getSupplierById(supplierId: String): Flow<SupplierEntity?>
    suspend fun insertSupplier(supplier: SupplierEntity)
    suspend fun updateSupplier(supplier: SupplierEntity)
    suspend fun deleteSupplier(supplier: SupplierEntity)
    suspend fun deleteAllSuppliers()

    // Order operations
    fun getAllOrders(): Flow<List<OrderEntity>>
    fun getOrderById(orderId: String): Flow<OrderEntity?>
    fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>>
    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<OrderEntity>>
    suspend fun insertOrder(order: OrderEntity)
    suspend fun updateOrder(order: OrderEntity)
    suspend fun deleteOrder(order: OrderEntity)

    // Order item operations
    fun getOrderItemsForOrder(orderId: String): Flow<List<OrderItemEntity>>
    suspend fun insertOrderItem(orderItem: OrderItemEntity)
    suspend fun insertAllOrderItems(orderItems: List<OrderItemEntity>)
    suspend fun updateOrderItem(orderItem: OrderItemEntity)
    suspend fun deleteOrderItem(orderItem: OrderItemEntity)
    suspend fun deleteAllOrderItemsForOrder(orderId: String)
    fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?>
    fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>>
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>)

    // User preferences
    fun getPreference(key: String): Flow<String?>
    suspend fun savePreference(key: String, value: String)
    suspend fun deletePreference(key: String)
    suspend fun deleteAllPreferences()

    // Warehouse operations
    fun getAllWarehouses(): Flow<List<WarehouseEntity>>
    fun getWarehouseById(warehouseId: String): Flow<WarehouseEntity?>
    suspend fun insertWarehouse(warehouse: WarehouseEntity)
    suspend fun updateWarehouse(warehouse: WarehouseEntity)
    suspend fun deleteWarehouse(warehouse: WarehouseEntity)
    suspend fun deleteAllWarehouses()

    // Location operations
    fun getAllLocations(): Flow<List<LocationEntity>>
    fun getLocationById(locationId: String): Flow<LocationEntity?>
    suspend fun insertLocation(location: LocationEntity)
    suspend fun updateLocation(location: LocationEntity)
    suspend fun deleteLocation(location: LocationEntity)
    suspend fun deleteAllLocations()

    // ProductLocation operations (nuevo modelo unificado de stock)
    fun getLocationsForProduct(productId: String): Flow<List<ProductLocationEntity>>
    fun getTotalStockForProduct(productId: String): Flow<Int?>
    suspend fun insertProductLocation(productLocation: ProductLocationEntity)
    suspend fun updateProductLocation(productLocation: ProductLocationEntity)
    suspend fun deleteProductLocation(productLocation: ProductLocationEntity)
    suspend fun deleteAllProductLocations()

    suspend fun addStockToLocation(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?,
        amount: Int
    )

    suspend fun transferStock(
        productId: String,
        fromLocationId: String,
        fromAisle: String?,
        fromShelf: String?,
        fromLevel: String?,
        toLocationId: String,
        toAisle: String?,
        toShelf: String?,
        toLevel: String?,
        amount: Int
    )

    // Firestore sync
    suspend fun syncProductToFirestore(product: ProductEntity): Result<Unit>
}
