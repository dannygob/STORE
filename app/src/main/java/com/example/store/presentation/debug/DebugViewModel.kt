package com.example.store.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _debugMessages = MutableStateFlow<List<String>>(emptyList())
    val debugMessages: StateFlow<List<String>> = _debugMessages.asStateFlow()

    init {
        addMessage("DebugViewModel Initialized.")
        testDatabaseOperations()
    }

    private fun addMessage(message: String) {
        _debugMessages.value = _debugMessages.value + message
        // Log.d("DebugViewModel", message) // Optional: Log to Logcat
    }

    fun testDatabaseOperations() {
        viewModelScope.launch {
            addMessage("Starting database operations...")

            // Clean up existing data for fresh test
            appRepository.deleteAllProducts()
            appRepository.deleteAllCustomers()
            appRepository.deleteAllSuppliers()
            addMessage("Cleared old data.")

            // Insert Sample Data
            val supplier1 = SupplierEntity(name = "MegaCorp Supplies")
            appRepository.insertSupplier(supplier1)
            addMessage("Inserted Supplier: ${supplier1.name}")

            val product1 = ProductEntity(name = "Super Widget", price = 19.99, stockQuantity = 100, supplierId = supplier1.id)
            val product2 = ProductEntity(name = "Hyper Gadget", price = 149.50, stockQuantity = 50)
            appRepository.insertAllProducts(listOf(product1, product2))
            addMessage("Inserted Products: ${product1.name}, ${product2.name}")

            val customer1 = CustomerEntity(name = "Alice Wonderland", email = "alice@example.com")
            appRepository.insertCustomer(customer1)
            addMessage("Inserted Customer: ${customer1.name}")

            // Fetch and Log Data
            appRepository.getAllProducts().collect { products ->
                addMessage("Fetched Products (${products.size}): ${products.joinToString { it.name }}")
            }
            // Note: Collecting multiple flows like this directly in init/one function can be tricky
            // for continuous updates. For a one-time test, it's okay, but typically you'd expose
            // individual flows to the UI or combine them more carefully.
            // For this placeholder, we'll let them run and collect initial values.
        }
        viewModelScope.launch {
            appRepository.getAllCustomers().collect { customers ->
                addMessage("Fetched Customers (${customers.size}): ${customers.joinToString { it.name }}")
            }
        }
        viewModelScope.launch {
            appRepository.getAllSuppliers().collect { suppliers ->
                addMessage("Fetched Suppliers (${suppliers.size}): ${suppliers.joinToString { it.name }}")
            }
        }
    }
}
