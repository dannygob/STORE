package com.example.store.data.repository

import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.SupplierEntity
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
}
