package com.example.store.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.data.local.entity.WarehouseEntity
import com.example.store.data.local.entity.StockAtWarehouseEntity // New
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
        testCoreDatabaseOperations()
        testOrderOperations()
        testUserPreferenceOperations()
        testWarehouseOperations()
        testStockAtWarehouseOperations() // New method call
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

            // Sync product1 to Firestore
            viewModelScope.launch {
                addMessage("Attempting to sync ${product1.name} to Firestore...")
                val syncResult = appRepository.syncProductToFirestore(product1)
                if (syncResult.isSuccess) {
                    addMessage("Successfully synced ${product1.name} to Firestore.")

                    // Attempt to read it back
                    viewModelScope.launch {
                        addMessage("Attempting to read ${product1.name} (ID: ${product1.id}) back from Firestore...")
                        appRepository.getProductFromFirestore(product1.id).collect { result ->
                            if (result.isSuccess) {
                                val fetchedProduct = result.getOrNull()
                                if (fetchedProduct != null) {
                                    addMessage("Successfully read ${fetchedProduct.name} from Firestore. Price: ${fetchedProduct.price}")
                                } else {
                                    addMessage("${product1.name} not found in Firestore after sync (or was null).")
                                }
                            } else {
                                addMessage("Failed to read ${product1.name} from Firestore: ${result.exceptionOrNull()?.message}")
                            }
                        }
                    }
                } else {
                    addMessage("Failed to sync ${product1.name} to Firestore: ${syncResult.exceptionOrNull()?.message}")
                }
            }

            val customer1 = CustomerEntity(name = "Alice Wonderland", email = "alice@example.com")
            appRepository.insertCustomer(customer1)
            addMessage("Inserted Customer: ${customer1.name}")

            // Fetch and Log Data
            appRepository.getAllProducts().collect { products ->
                addMessage("Fetched Products (${products.size}): ${products.joinToString { it.name }}")
            }

            // Test Product Search
            addMessage("Searching for products with 'Widget'...")
            appRepository.searchProductsByName("Widget").collect { searchResults ->
                addMessage("Search Results for 'Widget' (${searchResults.size}): ${searchResults.joinToString { it.name }}")
            }
            addMessage("Searching for products with 'Gadget'...")
            appRepository.searchProductsByName("Gadget").collect { searchResults ->
                addMessage("Search Results for 'Gadget' (${searchResults.size}): ${searchResults.joinToString { it.name }}")
            }
            addMessage("Searching for products with 'NonExistent'...")
            appRepository.searchProductsByName("NonExistent").collect { searchResults ->
                addMessage("Search Results for 'NonExistent' (${searchResults.size}): ${searchResults.joinToString { it.name }}")
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

    fun testWarehouseOperations() {
        viewModelScope.launch {
            addMessage("Starting warehouse database operations...")

            // Clean up existing warehouses first for a clean test run
            appRepository.deleteAllWarehouses()
            addMessage("Deleted all existing warehouses.")

            // Insert a sample warehouse
            val warehouse1 = com.example.store.data.local.entity.WarehouseEntity(name = "Main Warehouse", address = "123 Storage Rd", capacity = 1000.0, notes = "Primary facility")
            appRepository.insertWarehouse(warehouse1)
            addMessage("Inserted Warehouse: Name='${warehouse1.name}', ID='${warehouse1.warehouseId}', Notes='${warehouse1.notes}'")

            // Fetch the warehouse by ID
            appRepository.getWarehouseById(warehouse1.warehouseId).collect { fetchedWarehouse ->
                if (fetchedWarehouse != null) {
                    addMessage("Fetched Warehouse by ID: Name='${fetchedWarehouse.name}', Address='${fetchedWarehouse.address}', Notes='${fetchedWarehouse.notes}'")
                } else {
                    addMessage("Warehouse with ID '${warehouse1.warehouseId}' not found after insert.")
                }
            }

            // Insert another warehouse
            val warehouse2 = com.example.store.data.local.entity.WarehouseEntity(name = "North Depot", address = "456 Distribution Ave", notes = null) // Notes can be null
            appRepository.insertWarehouse(warehouse2)
            addMessage("Inserted Warehouse: Name='${warehouse2.name}', ID='${warehouse2.warehouseId}', Notes='${warehouse2.notes}'")

            // Fetch all warehouses
            appRepository.getAllWarehouses().collect { warehouses ->
                addMessage("Fetched All Warehouses (${warehouses.size}):")
                warehouses.forEach { wh ->
                    addMessage("  - ID='${wh.warehouseId}', Name='${wh.name}', Address='${wh.address}', Notes='${wh.notes}'")
                }
            }
        }
    }

    fun testStockAtWarehouseOperations() {
        viewModelScope.launch {
            addMessage("Starting StockAtWarehouse operations...")

            // --- Prerequisites: Ensure a product and a warehouse exist ---
            var testProductId: String? = null
            var testWarehouseId: String? = null

            // Get a product (assuming one was created in testCoreDatabaseOperations)
            appRepository.getAllProducts().collect { products ->
                if (products.isNotEmpty()) {
                    testProductId = products.first().id
                    addMessage("Using Product ID for stock test: $testProductId (${products.first().name})")
                }
            }
            if (testProductId == null) { // Create if not found
                val newProd = ProductEntity(name = "Stock Test Product", price = 5.0, stockQuantity = 0) // Overall stock can be 0
                appRepository.insertProduct(newProd)
                testProductId = newProd.id
                addMessage("Created Product ID for stock test: $testProductId")
            }

            // Get a warehouse (assuming one was created in testWarehouseOperations)
            appRepository.getAllWarehouses().collect { warehouses ->
                if (warehouses.isNotEmpty()) {
                    testWarehouseId = warehouses.first().warehouseId
                    addMessage("Using Warehouse ID for stock test: $testWarehouseId (${warehouses.first().name})")
                }
            }
             if (testWarehouseId == null) { // Create if not found
                val newWh = com.example.store.data.local.entity.WarehouseEntity(name = "Stock Test Warehouse")
                appRepository.insertWarehouse(newWh)
                testWarehouseId = newWh.warehouseId
                addMessage("Created Warehouse ID for stock test: $testWarehouseId")
            }
            // --- End Prerequisites ---

            if (testProductId == null || testWarehouseId == null) {
                addMessage("Failed to setup prerequisites for StockAtWarehouse test. Aborting.")
                return@launch
            }

            // 1. Insert Stock
            val initialStock = StockAtWarehouseEntity(productId = testProductId!!, warehouseId = testWarehouseId!!, quantity = 100)
            appRepository.insertStockAtWarehouse(initialStock)
            addMessage("Inserted initial stock for ProdID $testProductId in WhID $testWarehouseId: Qty ${initialStock.quantity}")

            // 2. Fetch specific stock record
            appRepository.getStockForProductInWarehouse(testProductId!!, testWarehouseId!!).collect { stock ->
                addMessage("Fetched stock for ProdID $testProductId in WhID $testWarehouseId: Qty ${stock?.quantity ?: "Not found"}")
            }

            // 3. Update Stock
            val updatedStock = initialStock.copy(quantity = 150)
            appRepository.updateStockAtWarehouse(updatedStock)
            addMessage("Updated stock for ProdID $testProductId in WhID $testWarehouseId to Qty ${updatedStock.quantity}")
            appRepository.getStockForProductInWarehouse(testProductId!!, testWarehouseId!!).collect { stock ->
                 addMessage("Fetched updated stock: Qty ${stock?.quantity ?: "Not found"}")
            }

            // 4. Add stock for the same product in a new warehouse to test total quantity
            val warehouse2 = com.example.store.data.local.entity.WarehouseEntity(name = "Secondary Stock WH")
            appRepository.insertWarehouse(warehouse2)
            addMessage("Inserted warehouse ${warehouse2.name} for multi-stock test.")
            val stockInWh2 = StockAtWarehouseEntity(productId = testProductId!!, warehouseId = warehouse2.warehouseId, quantity = 75)
            appRepository.insertStockAtWarehouse(stockInWh2)
            addMessage("Inserted stock for ProdID $testProductId in WhID ${warehouse2.warehouseId}: Qty ${stockInWh2.quantity}")


            // 5. Get all stock for the product
            appRepository.getAllStockForProduct(testProductId!!).collect { stocks ->
                addMessage("All stock locations for ProdID $testProductId (${stocks.size}):")
                stocks.forEach { s -> addMessage("  WhID ${s.warehouseId}: Qty ${s.quantity}") }
            }

            // 6. Get all stock in a warehouse
            appRepository.getAllStockInWarehouse(testWarehouseId!!).collect { stocks ->
                addMessage("All stock in WhID $testWarehouseId (${stocks.size}):")
                stocks.forEach { s -> addMessage("  ProdID ${s.productId}: Qty ${s.quantity}") }
            }

            // 7. Get total stock quantity for the product
            appRepository.getTotalStockQuantityForProduct(testProductId!!).collect { totalQty ->
                addMessage("Total stock quantity for ProdID $testProductId across all warehouses: ${totalQty ?: 0}")
            }

            // 8. Delete a specific stock record
            appRepository.deleteStockAtWarehouse(updatedStock) // Delete the stock from the first warehouse
            addMessage("Deleted stock for ProdID $testProductId from WhID $testWarehouseId.")
            appRepository.getStockForProductInWarehouse(testProductId!!, testWarehouseId!!).collect { stock ->
                 addMessage("Stock for ProdID $testProductId in WhID $testWarehouseId after delete: ${stock?.quantity ?: "Not found (Correct)"}")
            }
            appRepository.getTotalStockQuantityForProduct(testProductId!!).collect { totalQty ->
                addMessage("Total stock for ProdID $testProductId after deleting one record: ${totalQty ?: 0}")
            }

            // 9. Delete all stock (for cleanup in debug)
            // appRepository.deleteAllStockAtWarehouse()
            // addMessage("Deleted all stock records from stock_at_warehouse table.")
            // appRepository.getTotalStockQuantityForProduct(testProductId!!).collect { totalQty ->
            //     addMessage("Total stock for ProdID $testProductId after deleteAll: ${totalQty ?: "0 (Correct)"}")
            // }
        }
    }

    fun testUserPreferenceOperations() {
        viewModelScope.launch {
            addMessage("Starting user preference database operations...")

            val testThemeKey = "user_theme"
            val testThemeValue = "dark_mode_v1"

            // Save a preference
            appRepository.savePreference(testThemeKey, testThemeValue)
            addMessage("Saved preference: Key='$testThemeKey', Value='$testThemeValue'")

            // Fetch the preference
            appRepository.getPreference(testThemeKey).collect { fetchedValue ->
                if (fetchedValue != null) {
                    addMessage("Fetched preference: Key='$testThemeKey', Value='$fetchedValue'")
                } else {
                    addMessage("Preference for Key='$testThemeKey' not found after save.")
                }
            }

            // Test deleting a preference
            val tempKey = "temp_pref_to_delete"
            appRepository.savePreference(tempKey, "some_value")
            addMessage("Saved temp preference: Key='$tempKey'")
            appRepository.deletePreference(tempKey)
            addMessage("Deleted temp preference: Key='$tempKey'")
            appRepository.getPreference(tempKey).collect { deletedValue ->
                if (deletedValue == null) {
                    addMessage("Successfully verified deletion of Key='$tempKey'.")
                } else {
                    addMessage("ERROR: Key='$tempKey' still exists after deletion. Value='$deletedValue'")
                }
            }

            // Test deleting all (if desired, be careful with this in real apps without confirmation)
            // For debug purposes, let's save one more and then delete all
            appRepository.savePreference("another_key", "another_value")
            addMessage("Saved 'another_key' before deleteAll.")
            appRepository.deleteAllPreferences()
            addMessage("Called deleteAllPreferences().")
            appRepository.getPreference(testThemeKey).collect{themeVal -> // Check original key
                appRepository.getPreference("another_key").collect{anotherVal ->
                    if(themeVal == null && anotherVal == null) {
                        addMessage("Verified: All preferences deleted (checked '$testThemeKey' and 'another_key').")
                    } else {
                        addMessage("ERROR: Preferences not all deleted. '$testThemeKey': $themeVal, 'another_key': $anotherVal")
                    }
                }
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
                     addMessage("Order: ID=${orderWithItems.order.orderId}, CustID=${orderWithItems.order.customerId}, Status=${orderWithItems.order.status}, Total=${orderWithItems.order.totalAmount}, Date=${orderWithItems.order.orderDate}")
                     orderWithItems.items.forEach { item ->
                         addMessage("  Item: ProdID=${item.productId}, Qty=${item.quantity}, Price=${item.pricePerUnit}")
                     }
                 }
                 addMessage("--- End of All Orders ---")
            }

            // Test Order Date Range Query
            val today = System.currentTimeMillis()
            val yesterday = today - (24 * 60 * 60 * 1000)
            val dayBeforeYesterday = yesterday - (24 * 60 * 60 * 1000)
            val tomorrow = today + (24 * 60 * 60 * 1000)

            // Clean up orders for this specific test section for clarity
            appRepository.deleteAllOrderItemsForOrder(order1.orderId) // Assuming order1 is still in scope
            appRepository.deleteOrder(order1)

            val orderYesterday = OrderEntity(customerId = testCustomerId, status = "Delivered", totalAmount = 50.0, orderDate = yesterday)
            val orderToday = OrderEntity(customerId = testCustomerId, status = "Processing", totalAmount = 75.0, orderDate = today)
            appRepository.insertOrder(orderYesterday)
            appRepository.insertOrder(orderToday)
            addMessage("Inserted order for yesterday (ID ${orderYesterday.orderId}) and today (ID ${orderToday.orderId}) for date range test.")

            addMessage("Fetching orders from dayBeforeYesterday to today (should include 2 orders)...")
            appRepository.getOrdersByDateRange(dayBeforeYesterday, today).collect { dateRangeOrders ->
                addMessage("Orders in range D-2 to Today (${dateRangeOrders.size}):")
                dateRangeOrders.forEach { order -> addMessage("  Order ID: ${order.orderId}, Date: ${order.orderDate}, Status: ${order.status}")}
            }

            addMessage("Fetching orders for just yesterday (should include 1 order)...")
            appRepository.getOrdersByDateRange(dayBeforeYesterday, yesterday).collect { dateRangeOrders ->
                 addMessage("Orders in range D-2 to Yesterday (${dateRangeOrders.size}):")
                dateRangeOrders.forEach { order -> addMessage("  Order ID: ${order.orderId}, Date: ${order.orderDate}, Status: ${order.status}")}
            }

            addMessage("Fetching orders for future (should include 0 orders)...")
            appRepository.getOrdersByDateRange(tomorrow, tomorrow + (24*60*60*1000)).collect { dateRangeOrders ->
                addMessage("Orders in future range (${dateRangeOrders.size}):")
                dateRangeOrders.forEach { order -> addMessage("  Order ID: ${order.orderId}, Date: ${order.orderDate}, Status: ${order.status}")}
            }
        }
    }
}
