package com.example.store.data.repository

import com.example.store.data.local.dao.CustomerDao
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow

// In a real app, DAOs would likely be injected (e.g., using Hilt)
class AppRepositoryImpl(
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val supplierDao: SupplierDao
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
}
