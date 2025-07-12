package com.example.Store.data.repository

import com.example.Store.data.local.AppDatabase
import com.example.Store.data.local.dao.*
import com.example.Store.data.local.entity.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val supplierDao: SupplierDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val userPreferenceDao: UserPreferenceDao,
    private val warehouseDao: WarehouseDao,
    private val stockAtWarehouseDao: StockAtWarehouseDao,
    private val firestore: FirebaseFirestore,
) : AppRepository {

    override fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    override fun getProductById(productId: String): Flow<ProductEntity?> = productDao.getProductById(productId)
    override suspend fun insertProduct(product: ProductEntity) = productDao.insert(product)
    override suspend fun updateProduct(product: ProductEntity) = productDao.update(product)
    override suspend fun deleteProduct(product: ProductEntity) = productDao.delete(product)
    override suspend fun insertAllProducts(products: List<ProductEntity>) = productDao.insertAll(products)
    override suspend fun deleteAllProducts() = productDao.deleteAllProducts()
    override fun searchProductsByName(query: String): Flow<List<ProductEntity>> = productDao.searchProductsByName(query)

    override fun getAllCustomers(): Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    override fun getCustomerById(customerId: String): Flow<CustomerEntity?> = customerDao.getCustomerById(customerId)
    override fun getCustomerByEmail(email: String): Flow<CustomerEntity?> = customerDao.getCustomerByEmail(email)
    override suspend fun insertCustomer(customer: CustomerEntity) = customerDao.insert(customer)
    override suspend fun updateCustomer(customer: CustomerEntity) = customerDao.update(customer)
    override suspend fun deleteCustomer(customer: CustomerEntity) = customerDao.delete(customer)
    override suspend fun deleteAllCustomers() = customerDao.deleteAllCustomers()

    override fun getAllSuppliers(): Flow<List<SupplierEntity>> = supplierDao.getAllSuppliers()
    override fun getSupplierById(supplierId: String): Flow<SupplierEntity?> = supplierDao.getSupplierById(supplierId)
    override suspend fun insertSupplier(supplier: SupplierEntity) = supplierDao.insert(supplier)
    override suspend fun updateSupplier(supplier: SupplierEntity) = supplierDao.update(supplier)
    override suspend fun deleteSupplier(supplier: SupplierEntity) = supplierDao.delete(supplier)
    override suspend fun deleteAllSuppliers() = supplierDao.deleteAllSuppliers()

    override fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    override fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)
    override fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByCustomerId(customerId)
    override fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<OrderEntity>> = orderDao.getOrdersByDateRange(startDate, endDate)
    override suspend fun insertOrder(order: OrderEntity) = orderDao.insertOrder(order)
    override suspend fun updateOrder(order: OrderEntity) = orderDao.updateOrder(order)
    override suspend fun deleteOrder(order: OrderEntity) = orderDao.deleteOrder(order)

    override fun getOrderItemsForOrder(orderId: String): Flow<List<OrderItemEntity>> = orderItemDao.getOrderItemsForOrder(orderId)
    override suspend fun insertOrderItem(orderItem: OrderItemEntity) = orderItemDao.insertOrderItem(orderItem)
    override suspend fun insertAllOrderItems(orderItems: List<OrderItemEntity>) = orderItemDao.insertAllOrderItems(orderItems)
    override suspend fun updateOrderItem(orderItem: OrderItemEntity) = orderItemDao.updateOrderItem(orderItem)
    override suspend fun deleteOrderItem(orderItem: OrderItemEntity) = orderItemDao.deleteOrderItem(orderItem)
    override suspend fun deleteAllOrderItemsForOrder(orderId: String) = orderItemDao.deleteAllOrderItemsForOrder(orderId)

    override fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?> = orderItemDao.getOrderWithOrderItems(orderId)
    override fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>> = orderItemDao.getAllOrdersWithOrderItems()

    override suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        appDatabase.runInTransaction {
            orderDao.insertOrder(order)
            orderItemDao.insertAllOrderItems(items.map { it.copy(orderId = order.orderId) })
        }
    }

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

    override fun getAllWarehouses(): Flow<List<WarehouseEntity>> = warehouseDao.getAllWarehouses()
    override fun getWarehouseById(warehouseId: String): Flow<WarehouseEntity?> = warehouseDao.getWarehouseById(warehouseId)
    override suspend fun insertWarehouse(warehouse: WarehouseEntity) = warehouseDao.insertWarehouse(warehouse)
    override suspend fun updateWarehouse(warehouse: WarehouseEntity) = warehouseDao.updateWarehouse(warehouse)
    override suspend fun deleteWarehouse(warehouse: WarehouseEntity) = warehouseDao.deleteWarehouse(warehouse)
    override suspend fun deleteAllWarehouses() = warehouseDao.deleteAllWarehouses()

    override fun getStockForProductInWarehouse(productId: String, warehouseId: String): Flow<StockAtWarehouseEntity?> =
        stockAtWarehouseDao.getStockForProductInWarehouse(productId, warehouseId)

    override fun getAllStockForProduct(productId: String): Flow<List<StockAtWarehouseEntity>> =
        stockAtWarehouseDao.getAllStockForProduct(productId)

    override fun getAllStockInWarehouse(warehouseId: String): Flow<List<StockAtWarehouseEntity>> =
        stockAtWarehouseDao.getAllStockInWarehouse(warehouseId)

    override fun getTotalStockQuantityForProduct(productId: String): Flow<Int?> =
        stockAtWarehouseDao.getTotalStockQuantityForProduct(productId)

    override suspend fun insertStockAtWarehouse(stock: StockAtWarehouseEntity) =
        stockAtWarehouseDao.insertStock(stock)

    override suspend fun updateStockAtWarehouse(stock: StockAtWarehouseEntity) =
        stockAtWarehouseDao.updateStock(stock)

    override suspend fun deleteStockAtWarehouse(stock: StockAtWarehouseEntity) =
        stockAtWarehouseDao.deleteStock(stock)

    override suspend fun deleteAllStockAtWarehouse() =
        stockAtWarehouseDao.deleteAllStock()

    override suspend fun syncProductToFirestore(product: ProductEntity): Result<Unit> {
        return try {
            firestore.collection("products").document(product.id)
                .set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProductFromFirestore(productId: String): Flow<Result<ProductEntity?>> =
        callbackFlow {
        val documentRef = firestore.collection("products").document(productId)
        val listenerRegistration = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                try {
                    val product = snapshot.toObject(ProductEntity::class.java)
                    trySend(Result.success(product))
                } catch (e: Exception) {
                    trySend(Result.failure(e))
                }
            } else {
                trySend(Result.success(null))
            }
        }
            awaitClose { listenerRegistration.remove() }
    }
}
