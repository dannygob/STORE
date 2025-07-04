package com.example.store.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.data.local.entity.OrderEntity // New
import com.example.store.data.local.entity.OrderItemEntity // New
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
        testCoreDatabaseOperations() // Renamed for clarity
        testOrderOperations()      // New method call
    }

    private fun addMessage(message: String) {
        _debugMessages.value = _debugMessages.value + message
        // Log.d("DebugViewModel", message) // Optional: Log to Logcat
    }

    fun testCoreDatabaseOperations() { // Renamed
        viewModelScope.launch {
            addMessage("Starting core database operations (Products, Customers, Suppliers)...")

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

    fun testOrderOperations() {
        viewModelScope.launch {
            addMessage("Starting order database operations...")

            // --- Prerequisites: Ensure a customer and some products exist ---
            // For simplicity, we'll try to fetch existing ones or insert new ones if needed.
            // In a real test, you might want more deterministic setup.

            var testCustomerId: String? = null
            appRepository.getAllCustomers().collect { customers ->
                if (customers.isNotEmpty()) {
                    testCustomerId = customers.first().id
                    addMessage("Using existing customer for order: ${customers.first().name}")
                }
            }
            if (testCustomerId == null) {
                val newCustomer = CustomerEntity(name = "Order Test Customer", email = "ordertest@example.com")
                appRepository.insertCustomer(newCustomer)
                testCustomerId = newCustomer.id
                addMessage("Inserted new customer for order: ${newCustomer.name}")
            }

            val testProductIds = mutableListOf<String>()
            val testProductPrices = mutableMapOf<String, Double>()

            appRepository.getAllProducts().collect { products ->
                if (products.size >= 2) {
                    testProductIds.add(products[0].id)
                    testProductPrices[products[0].id] = products[0].price
                    testProductIds.add(products[1].id)
                    testProductPrices[products[1].id] = products[1].price
                    addMessage("Using existing products for order: ${products[0].name}, ${products[1].name}")
                }
            }

            if (testProductIds.size < 2) {
                val newProduct1 = ProductEntity(name = "OrderTest Product A", price = 10.0, stockQuantity = 10)
                val newProduct2 = ProductEntity(name = "OrderTest Product B", price = 25.0, stockQuantity = 5)
                appRepository.insertAllProducts(listOf(newProduct1, newProduct2))
                testProductIds.clear() // Clear any partially filled list
                testProductPrices.clear()
                testProductIds.add(newProduct1.id)
                testProductPrices[newProduct1.id] = newProduct1.price
                testProductIds.add(newProduct2.id)
                testProductPrices[newProduct2.id] = newProduct2.price
                addMessage("Inserted new products for order: ${newProduct1.name}, ${newProduct2.name}")
            }
            // --- End Prerequisites ---

            if (testCustomerId == null || testProductIds.size < 2) {
                addMessage("Failed to setup prerequisites for order test. Aborting order operations.")
                return@launch
            }

            // 1. Create and Insert an Order
            val order1 = OrderEntity(
                customerId = testCustomerId,
                status = "Pending",
                totalAmount = (1 * testProductPrices[testProductIds[0]]!!) + (2 * testProductPrices[testProductIds[1]]!!) // Calculated based on items below
            )
            // appRepository.insertOrder(order1) // Using insertOrderWithItems instead
            // addMessage("Inserted Order ID: ${order1.orderId} for customer ID: $testCustomerId")

            // 2. Create and Insert OrderItems
            val orderItem1 = com.example.store.data.local.entity.OrderItemEntity(
                orderId = order1.orderId, // Will be set by insertOrderWithItems if not set here
                productId = testProductIds[0],
                quantity = 1,
                pricePerUnit = testProductPrices[testProductIds[0]]!!
            )
            val orderItem2 = com.example.store.data.local.entity.OrderItemEntity(
                orderId = order1.orderId, // Will be set by insertOrderWithItems if not set here
                productId = testProductIds[1],
                quantity = 2,
                pricePerUnit = testProductPrices[testProductIds[1]]!!
            )
            // appRepository.insertAllOrderItems(listOf(orderItem1, orderItem2))
            // addMessage("Inserted 2 order items for Order ID: ${order1.orderId}")

            // Use combined operation
            appRepository.insertOrderWithItems(order1, listOf(orderItem1, orderItem2))
            addMessage("Inserted Order ID: ${order1.orderId} with 2 items using combined operation.")


            // 3. Fetch the Order with its Items and Log
            appRepository.getOrderWithOrderItems(order1.orderId).collect { orderWithItems ->
                if (orderWithItems != null) {
                    addMessage("Fetched Order with Items: ID=${orderWithItems.order.orderId}, CustID=${orderWithItems.order.customerId}, Status=${orderWithItems.order.status}, Total=${orderWithItems.order.totalAmount}")
                    orderWithItems.items.forEach { item ->
                        addMessage("  Item: ProdID=${item.productId}, Qty=${item.quantity}, Price=${item.pricePerUnit}")
                    }
                } else {
                    addMessage("Order with ID ${order1.orderId} not found after insert.")
                }
            }

            // Fetch all orders with items to see the list
            appRepository.getAllOrdersWithOrderItems().collect { allOrdersWithItems ->
                 addMessage("--- All Orders with Items (${allOrdersWithItems.size}) ---")
                 allOrdersWithItems.forEach { orderWithItems ->
                     addMessage("Order: ID=${orderWithItems.order.orderId}, CustID=${orderWithItems.order.customerId}, Status=${orderWithItems.order.status}, Total=${orderWithItems.order.totalAmount}")
                     orderWithItems.items.forEach { item ->
                         addMessage("  Item: ProdID=${item.productId}, Qty=${item.quantity}, Price=${item.pricePerUnit}")
                     }
                 }
                 addMessage("--- End of All Orders ---")
            }
        }
    }
}
