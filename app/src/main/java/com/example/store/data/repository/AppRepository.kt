package com.example.store.data.repository


import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.LocationEntity
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.domain.model.OrderWithOrderItems
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    // Product Methods
    fun getAllProducts(): Flow<List<ProductEntity>>
    fun getProductById(productId: String?): Flow<ProductEntity?>
    suspend fun insertProduct(product: ProductEntity)
    suspend fun updateProduct(product: ProductEntity)
    suspend fun deleteProduct(product: ProductEntity)
    suspend fun insertAllProducts(products: List<ProductEntity>)
    suspend fun deleteAllProducts()
    fun searchProductsByName(query: String): Flow<List<ProductEntity>>

    // Customer Methods
    fun getAllCustomers(): Flow<List<CustomerEntity>>
    fun getCustomerById(customerId: String): Flow<CustomerEntity?>
    fun getCustomerByEmail(email: String): Flow<CustomerEntity?>
    suspend fun insertCustomer(customer: CustomerEntity)
    suspend fun updateCustomer(customer: CustomerEntity)
    suspend fun deleteCustomer(customer: CustomerEntity)
    suspend fun deleteAllCustomers()

    // Supplier Methods
    fun getAllSuppliers(): Flow<List<SupplierEntity>>
    fun getSupplierById(supplierId: String): Flow<SupplierEntity?>
    suspend fun insertSupplier(supplier: SupplierEntity)
    suspend fun updateSupplier(supplier: SupplierEntity)
    suspend fun deleteSupplier(supplier: SupplierEntity)
    suspend fun deleteAllSuppliers()

    // Order Methods
    fun getAllOrders(): Flow<List<OrderEntity>>
    fun getOrderById(orderId: String): Flow<OrderEntity?>
    fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>>
    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<OrderEntity>>
    suspend fun insertOrder(order: OrderEntity)
    suspend fun updateOrder(order: OrderEntity)
    suspend fun deleteOrder(order: OrderEntity)

    // OrderItem Methods
    fun getOrderItemsForOrder(orderId: String): Flow<List<OrderItemEntity>>
    suspend fun insertOrderItem(orderItem: OrderItemEntity)
    suspend fun insertAllOrderItems(orderItems: List<OrderItemEntity>)
    suspend fun updateOrderItem(orderItem: OrderItemEntity)
    suspend fun deleteOrderItem(orderItem: OrderItemEntity)
    suspend fun deleteAllOrderItemsForOrder(orderId: String)

    // Combined Operations
    fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?>
    fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>>
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>)

    // User Preference Methods
    fun getPreference(key: String): Flow<String?>
    suspend fun savePreference(key: String, value: String)
    suspend fun deletePreference(key: String)
    suspend fun deleteAllPreferences()

    // Location Methods
    fun getAllLocations(): Flow<List<LocationEntity>>
    fun getLocationById(locationId: String): Flow<LocationEntity?>
    suspend fun insertLocation(location: LocationEntity)
    suspend fun updateLocation(location: LocationEntity)
    suspend fun deleteLocation(location: LocationEntity)
    suspend fun deleteAllLocations()

    // ProductLocation Methods (nuevo sistema unificado de inventario)
    fun getLocationsForProduct(productId: String?): Flow<List<ProductLocationEntity>>
    fun getProductsAtLocation(locationId: String): Flow<List<ProductLocationEntity>>
    fun getTotalStockForProduct(productId: String): Flow<Int?>

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

    suspend fun insertProductLocation(productLocation: ProductLocationEntity)
    suspend fun updateProductLocation(productLocation: ProductLocationEntity)
    suspend fun deleteProductLocation(productLocation: ProductLocationEntity)
    suspend fun deleteAllProductLocations()

    // Firestore Sync Methods
    suspend fun syncProductToFirestore(product: ProductEntity): Result<Unit>
    fun getProductFromFirestore(productId: String): Flow<Result<ProductEntity?>>

    suspend fun syncLocationToFirestore(location: LocationEntity): Result<Unit>
    suspend fun syncProductLocationToFirestore(productLocation: ProductLocationEntity): Result<Unit>

    fun listenForLocationChanges()
    fun listenForProductLocationChanges()
}
