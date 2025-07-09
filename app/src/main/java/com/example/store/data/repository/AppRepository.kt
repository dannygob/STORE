package com.example.store.data.repository

import com.example.store.data.local.dao.OrderWithOrderItems // New import
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.data.local.entity.WarehouseEntity
import com.example.store.data.local.entity.StockAtWarehouseEntity // New import
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    // Product Methods
    fun getAllProducts(): Flow<List<ProductEntity>>
    fun getProductById(productId: String): Flow<ProductEntity?>
    suspend fun insertProduct(product: ProductEntity)
    suspend fun updateProduct(product: ProductEntity)
    suspend fun deleteProduct(product: ProductEntity)
    suspend fun insertAllProducts(products: List<ProductEntity>)
    suspend fun deleteAllProducts()
    fun searchProductsByName(query: String): Flow<List<ProductEntity>> // New method

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
    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<OrderEntity>> // New method
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

    // Warehouse Methods
    fun getAllWarehouses(): Flow<List<WarehouseEntity>>
    fun getWarehouseById(warehouseId: String): Flow<WarehouseEntity?>
    suspend fun insertWarehouse(warehouse: WarehouseEntity)
    suspend fun updateWarehouse(warehouse: WarehouseEntity)
    suspend fun deleteWarehouse(warehouse: WarehouseEntity)
    suspend fun deleteAllWarehouses()

    // StockAtWarehouse Methods
    fun getStockForProductInWarehouse(productId: String, warehouseId: String): Flow<StockAtWarehouseEntity?>
    fun getAllStockForProduct(productId: String): Flow<List<StockAtWarehouseEntity>>
    fun getAllStockInWarehouse(warehouseId: String): Flow<List<StockAtWarehouseEntity>>
    fun getTotalStockQuantityForProduct(productId: String): Flow<Int?>
    suspend fun insertStockAtWarehouse(stock: StockAtWarehouseEntity)
    suspend fun updateStockAtWarehouse(stock: StockAtWarehouseEntity)
    suspend fun deleteStockAtWarehouse(stock: StockAtWarehouseEntity)
    suspend fun deleteAllStockAtWarehouse()

    // Firestore Sync Methods
    suspend fun syncProductToFirestore(product: ProductEntity): Result<Unit> // Using Result for success/failure
    fun getProductFromFirestore(productId: String): Flow<Result<ProductEntity?>> // Using Flow and Result
}
