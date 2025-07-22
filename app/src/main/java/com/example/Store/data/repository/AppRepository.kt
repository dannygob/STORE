package com.example.Store.data.repository


import com.example.Store.data.local.dao.OrderWithOrderItems
import com.example.Store.data.local.entity.CustomerEntity
import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.local.entity.OrderEntity
import com.example.Store.data.local.entity.OrderItemEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.ProductLocationEntity
import com.example.Store.data.local.entity.SupplierEntity
import com.example.Store.data.local.entity.WarehouseEntity
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    // Product Methods
    fun getAllProducts(): Flow<Resource<List<ProductEntity>>>
    fun getProductById(productId: String): Flow<Resource<ProductEntity?>>
    fun insertProduct(product: ProductEntity): Flow<Resource<Unit>>
    fun updateProduct(product: ProductEntity): Flow<Resource<Unit>>
    fun deleteProduct(product: ProductEntity): Flow<Resource<Unit>>
    fun insertAllProducts(products: List<ProductEntity>): Flow<Resource<Unit>>
    fun deleteAllProducts(): Flow<Resource<Unit>>
    fun searchProductsByName(query: String): Flow<Resource<List<ProductEntity>>>

    // Customer Methods
    fun getAllCustomers(): Flow<Resource<List<CustomerEntity>>>
    fun getCustomerById(customerId: String): Flow<Resource<CustomerEntity?>>
    fun getCustomerByEmail(email: String): Flow<Resource<CustomerEntity?>>
    fun insertCustomer(customer: CustomerEntity): Flow<Resource<Unit>>
    fun updateCustomer(customer: CustomerEntity): Flow<Resource<Unit>>
    fun deleteCustomer(customer: CustomerEntity): Flow<Resource<Unit>>
    fun deleteAllCustomers(): Flow<Resource<Unit>>

    // Supplier Methods
    fun getAllSuppliers(): Flow<Resource<List<SupplierEntity>>>
    fun getSupplierById(supplierId: String): Flow<Resource<SupplierEntity?>>
    fun insertSupplier(supplier: SupplierEntity): Flow<Resource<Unit>>
    fun updateSupplier(supplier: SupplierEntity): Flow<Resource<Unit>>
    fun deleteSupplier(supplier: SupplierEntity): Flow<Resource<Unit>>
    fun deleteAllSuppliers(): Flow<Resource<Unit>>

    // Order Methods
    fun getAllOrders(): Flow<Resource<List<OrderEntity>>>
    fun getOrderById(orderId: String): Flow<Resource<OrderEntity?>>
    fun getOrdersByCustomerId(customerId: String): Flow<Resource<List<OrderEntity>>>
    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<Resource<List<OrderEntity>>>
    fun insertOrder(order: OrderEntity): Flow<Resource<Unit>>
    fun updateOrder(order: OrderEntity): Flow<Resource<Unit>>
    fun deleteOrder(order: OrderEntity): Flow<Resource<Unit>>

    // OrderItem Methods
    fun getOrderItemsForOrder(orderId: String): Flow<Resource<List<OrderItemEntity>>>
    fun insertOrderItem(orderItem: OrderItemEntity): Flow<Resource<Unit>>
    fun insertAllOrderItems(orderItems: List<OrderItemEntity>): Flow<Resource<Unit>>
    fun updateOrderItem(orderItem: OrderItemEntity): Flow<Resource<Unit>>
    fun deleteOrderItem(orderItem: OrderItemEntity): Flow<Resource<Unit>>
    fun deleteAllOrderItemsForOrder(orderId: String): Flow<Resource<Unit>>

    // Combined Operations
    fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?>
    fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>>
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>)

    // User Preference Methods
    fun getPreference(key: String): Flow<String?>
    suspend fun savePreference(key: String, value: String)
    suspend fun deletePreference(key: String)
    suspend fun deleteAllPreferences()

    // Warehouse Methods
    fun getAllWarehouses(): Flow<Resource<List<WarehouseEntity>>>
    fun getWarehouseById(warehouseId: String): Flow<Resource<WarehouseEntity?>>
    fun insertWarehouse(warehouse: WarehouseEntity): Flow<Resource<Unit>>
    fun updateWarehouse(warehouse: WarehouseEntity): Flow<Resource<Unit>>
    fun deleteWarehouse(warehouse: WarehouseEntity): Flow<Resource<Unit>>
    fun deleteAllWarehouses(): Flow<Resource<Unit>>

    // Location Methods
    fun getAllLocations(): Flow<Resource<List<LocationEntity>>>
    fun getLocationById(locationId: String): Flow<Resource<LocationEntity?>>
    fun insertLocation(location: LocationEntity): Flow<Resource<Unit>>
    fun updateLocation(location: LocationEntity): Flow<Resource<Unit>>
    fun deleteLocation(location: LocationEntity): Flow<Resource<Unit>>
    fun deleteAllLocations(): Flow<Resource<Unit>>

    // ProductLocation Methods (nuevo sistema unificado de inventario)
    fun getLocationsForProduct(productId: String): Flow<Resource<List<ProductLocationEntity>>>
    fun getProductsAtLocation(locationId: String): Flow<Resource<List<ProductLocationEntity>>>
    fun getTotalStockForProduct(productId: String): Flow<Resource<Int?>>

    fun addStockToLocation(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?,
        amount: Int
    ): Flow<Resource<Unit>>

    fun transferStock(
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
    ): Flow<Resource<Unit>>

    fun insertProductLocation(productLocation: ProductLocationEntity): Flow<Resource<Unit>>
    fun updateProductLocation(productLocation: ProductLocationEntity): Flow<Resource<Unit>>
    fun deleteProductLocation(productLocation: ProductLocationEntity): Flow<Resource<Unit>>
    fun deleteAllProductLocations(): Flow<Resource<Unit>>

    // Firestore Sync Methods
    suspend fun syncProductToFirestore(product: ProductEntity): Result<Unit>
    fun getProductFromFirestore(productId: String): Flow<Result<ProductEntity?>>

    suspend fun syncLocationToFirestore(location: LocationEntity): Result<Unit>
    suspend fun syncProductLocationToFirestore(productLocation: ProductLocationEntity): Result<Unit>

    fun listenForLocationChanges()
    fun listenForProductLocationChanges()
}
