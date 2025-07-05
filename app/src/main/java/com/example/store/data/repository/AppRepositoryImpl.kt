package com.example.store.data.repository

import com.example.store.data.local.dao.CustomerDao
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.local.dao.OrderDao
import com.example.store.data.local.dao.OrderItemDao
import com.example.store.data.local.dao.OrderWithOrderItems
import com.example.store.data.local.dao.UserPreferenceDao // New import
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.data.local.entity.UserPreferenceEntity // New import
import kotlinx.coroutines.flow.Flow

// In a real app, DAOs would likely be injected (e.g., using Hilt)
class AppRepositoryImpl(
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val supplierDao: SupplierDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val userPreferenceDao: UserPreferenceDao // New DAO
) : AppRepository {

    // Product Methods
    override fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    override fun getProductById(productId: String): Flow<ProductEntity?> = productDao.getProductById(productId)
    override suspend fun insertProduct(product: ProductEntity) = productDao.insert(product)
    override suspend fun updateProduct(product: ProductEntity) = productDao.update(product)
    override suspend fun deleteProduct(product: ProductEntity) = productDao.delete(product)
    override suspend fun insertAllProducts(products: List<ProductEntity>) = productDao.insertAll(products)
    override suspend fun deleteAllProducts() = productDao.deleteAllProducts()

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
        orderDao.insertOrder(order)
        orderItemDao.insertAllOrderItems(items.map { it.copy(orderId = order.orderId) }) // Ensure items have correct orderId
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
}
