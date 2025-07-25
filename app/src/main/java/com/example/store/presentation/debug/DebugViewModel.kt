package com.example.store.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.LocationEntity
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
        testLocationOperations()
        testProductLocationOperations()
    }

    private fun addMessage(message: String) {
        _debugMessages.value = _debugMessages.value + message
    }

    fun testCoreDatabaseOperations() {
        viewModelScope.launch {
            addMessage("Starting core database operations...")

            appRepository.deleteAllProducts()
            appRepository.deleteAllCustomers()
            appRepository.deleteAllSuppliers()
            addMessage("Cleared old data.")

            val supplier1 = SupplierEntity(
                name = "MegaCorp Supplies",
                contactPerson = "John Doe",
                email = "contact@megacorp.com",
                phone = "123-456-7890"
            )
            appRepository.insertSupplier(supplier1)
            addMessage("Inserted Supplier: ${supplier1.name}")

            val product1 = ProductEntity(
                name = "Super Widget",
                description = "A super widget",
                category = "Widgets",
                imageUrl = "https://example.com/widget.jpg",
                price = 19.99,
                stockQuantity = 100,
                supplierId = supplier1.id
            )
            val product2 = ProductEntity(
                name = "Hyper Gadget",
                description = "A hyper gadget",
                category = "Gadgets",
                imageUrl = "https://example.com/gadget.jpg",
                price = 149.50,
                stockQuantity = 50,
                supplierId = supplier1.id
            )
            appRepository.insertAllProducts(listOf(product1, product2))
            addMessage("Inserted Products: ${product1.name}, ${product2.name}")

            val customer1 = CustomerEntity(
                name = "Alice Wonderland",
                email = "alice@example.com",
                phone = "555-123-4567",
                addressLine1 = "456 Elm St",
                addressLine2 = null,
                city = "Wonderland",
                postalCode = "67890",
                country = "Fantasy",
                latitude = 0.0,
                longitude = 0.0
            )
            appRepository.insertCustomer(customer1)
            addMessage("Inserted Customer: ${customer1.name}")

            appRepository.getAllProducts().collect { products ->
                addMessage("Fetched Products (${products.size}): ${products.joinToString { it.name }}")
            }

            appRepository.searchProductsByName("Widget").collect { searchResults ->
                addMessage("Search Results for 'Widget' (${searchResults.size}): ${searchResults.joinToString { it.name }}")
            }
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

    fun testLocationOperations() {
        viewModelScope.launch {
            addMessage("Starting location database operations...")

            appRepository.deleteAllLocations()
            addMessage("Deleted all existing locations.")

            val location1 = LocationEntity(
                name = "Main Warehouse",
                address = "123 Storage Rd",
                notes = "Primary facility",
                locationId = TODO(),
                capacity = TODO()
            )
            appRepository.insertLocation(location1)
            addMessage("Inserted Location: Name='${location1.name}', ID='${location1.locationId}', Notes='${location1.notes}'")

            appRepository.getLocationById(location1.locationId).collect { fetchedLocation ->
                if (fetchedLocation != null) {
                    addMessage("Fetched Location by ID: Name='${fetchedLocation.name}', Address='${fetchedLocation.address}', Notes='${fetchedLocation.notes}'")
                } else {
                    addMessage("Location with ID '${location1.locationId}' not found after insert.")
                }
            }

            val location2 = LocationEntity(
                name = "North Depot",
                address = "456 Distribution Ave",
                notes = null,
                locationId = TODO(),
                capacity = TODO()
            )
            appRepository.insertLocation(location2)
            addMessage("Inserted Location: Name='${location2.name}', ID='${location2.locationId}', Notes='${location2.notes}'")

            appRepository.getAllLocations().collect { locations ->
                addMessage("Fetched All Locations (${locations.size}):")
                locations.forEach { loc ->
                    addMessage("  - ID='${loc.locationId}', Name='${loc.name}', Address='${loc.address}', Notes='${loc.notes}'")
                }
            }
        }
    }

    fun testProductLocationOperations() {
        viewModelScope.launch {
            addMessage("Starting ProductLocation operations...")

            var testProductId: String? = null
            var testLocationId: String? = null

            val products = appRepository.getAllProducts().first()
            if (products.isNotEmpty()) {
                testProductId = products.first().id
                addMessage("Using Product ID for stock test: $testProductId (${products.first().name})")
            }
            if (testProductId == null) {
                val newProd = ProductEntity(
                    name = "Stock Test Product",
                    description = "Stock Test Product",
                    category = "Test",
                    imageUrl = "https://example.com/test.jpg",
                    price = 5.0,
                    stockQuantity = 0,
                    supplierId = "test"
                )
                appRepository.insertProduct(newProd)
                testProductId = newProd.id
                addMessage("Created Product ID for stock test: $testProductId")
            }

            val locations = appRepository.getAllLocations().first()
            if (locations.isNotEmpty()) {
                testLocationId = locations.first().locationId
                addMessage("Using Location ID for stock test: $testLocationId (${locations.first().name})")
            }
            if (testLocationId == null) {
                val newLoc =
                    LocationEntity(
                        name = "Stock Test Location",
                        address = "Test Address",
                        notes = "Test Notes",
                        locationId = TODO(),
                        capacity = TODO()
                    )
                appRepository.insertLocation(newLoc)
                testLocationId = newLoc.locationId
                addMessage("Created Location ID for stock test: $testLocationId")
            }

            // 1. Insert Stock
            val initialStock = ProductLocationEntity(
                productId = testProductId,
                locationId = testLocationId,
                quantity = 100,
                productLocationId = TODO(),
                aisle = TODO(),
                shelf = TODO(),
                level = TODO()
            )
            appRepository.insertProductLocation(initialStock)
            addMessage("Inserted initial stock for ProdID $testProductId in LocID $testLocationId: Qty ${initialStock.quantity}")

            // 2. Fetch specific stock record
            appRepository.getLocationsForProduct(testProductId).collect { stock ->
                addMessage("Fetched stock for ProdID $testProductId in LocID $testLocationId: Qty ${stock.firstOrNull()?.quantity ?: "Not found"}")
            }

            // 3. Update Stock
            val updatedStock = initialStock.copy(quantity = 150)
            appRepository.updateProductLocation(updatedStock)
            addMessage("Updated stock for ProdID $testProductId in LocID $testLocationId to Qty ${updatedStock.quantity}")
            appRepository.getLocationsForProduct(testProductId).collect { stock ->
                addMessage("Fetched updated stock: Qty ${stock.firstOrNull()?.quantity ?: "Not found"}")
            }

            // 4. Add stock for the same product in a new location to test total quantity
            val location2 =
                LocationEntity(
                    name = "Secondary Stock WH",
                    address = "Test Address 2",
                    notes = "Test Notes 2",
                    locationId = TODO(),
                    capacity = TODO()
                )
            appRepository.insertLocation(location2)
            addMessage("Inserted location ${location2.name} for multi-stock test.")
            val stockInLoc2 = ProductLocationEntity(
                productId = testProductId,
                locationId = location2.locationId,
                quantity = 75,
                productLocationId = TODO(),
                aisle = TODO(),
                shelf = TODO(),
                level = TODO()
            )
            appRepository.insertProductLocation(stockInLoc2)
            addMessage("Inserted stock for ProdID $testProductId in LocID ${location2.locationId}: Qty ${stockInLoc2.quantity}")


            // 5. Get all stock for the product
            appRepository.getLocationsForProduct(testProductId).collect { stocks ->
                addMessage("All stock locations for ProdID $testProductId (${stocks.size}):")
                stocks.forEach { s -> addMessage("  LocID ${s.locationId}: Qty ${s.quantity}") }
            }

            // 6. Get all stock in a location
            appRepository.getProductsAtLocation(testLocationId).collect { stocks ->
                addMessage("All stock in LocID $testLocationId (${stocks.size}):")
                stocks.forEach { s -> addMessage("  ProdID ${s.productId}: Qty ${s.quantity}") }
            }

            // 7. Get total stock quantity for the product
            appRepository.getTotalStockForProduct(testProductId).collect { totalQty ->
                addMessage("Total stock quantity for ProdID $testProductId across all locations: ${totalQty ?: 0}")
            }

            // 8. Delete a specific stock record
            appRepository.deleteProductLocation(updatedStock) // Delete the stock from the first location
            addMessage("Deleted stock for ProdID $testProductId from LocID $testLocationId.")
            appRepository.getLocationsForProduct(testProductId).collect { stock ->
                addMessage("Stock for ProdID $testProductId in LocID $testLocationId after delete: ${stock.firstOrNull()?.quantity ?: "Not found (Correct)"}")
            }
            appRepository.getTotalStockForProduct(testProductId).collect { totalQty ->
                addMessage("Total stock for ProdID $testProductId after deleting one record: ${totalQty ?: 0}")
            }
        }
    }

    fun testUserPreferenceOperations() {
        viewModelScope.launch {
            addMessage("Starting user preference database operations...")

            val testThemeKey = "user_theme"
            val testThemeValue = "dark_mode_v1"

            appRepository.savePreference(testThemeKey, testThemeValue)
            addMessage("Saved preference: Key='$testThemeKey', Value='$testThemeValue'")

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
            appRepository.getPreference(testThemeKey).collect { themeVal ->
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
            val customers = appRepository.getAllCustomers().first()
            if (customers.isNotEmpty()) {
                testCustomerId = customers.first().id
                addMessage("Using existing customer for order: ${customers.first().name}")
            }
            if (testCustomerId == null) {
                val newCustomer = CustomerEntity(
                    name = "Order Test Customer",
                    email = "ordertest@example.com",
                    phone = "555-123-4567",
                    addressLine1 = "Test Address",
                    addressLine2 = null,
                    city = "Test City",
                    postalCode = "12345",
                    country = "Test Country",
                    latitude = 0.0,
                    longitude = 0.0
                )
                appRepository.insertCustomer(newCustomer)
                testCustomerId = newCustomer.id
                addMessage("Inserted new customer for order: ${newCustomer.name}")
            }

            val testProductIds = mutableListOf<String>()
            val testProductPrices = mutableMapOf<String, Double>()

            val products = appRepository.getAllProducts().first()
            if (products.size >= 2) {
                testProductIds.add(products[0].id)
                testProductPrices[products[0].id] = products[0].price
                testProductIds.add(products[1].id)
                testProductPrices[products[1].id] = products[1].price
                addMessage("Using existing products for order: ${products[0].name}, ${products[1].name}")
            }

            if (testProductIds.size < 2) {
                val newProduct1 = ProductEntity(
                    name = "OrderTest Product A",
                    description = "Test Product A",
                    category = "Test",
                    imageUrl = "https://example.com/test.jpg",
                    price = 10.0,
                    stockQuantity = 10,
                    supplierId = "test"
                )
                val newProduct2 = ProductEntity(
                    name = "OrderTest Product B",
                    description = "Test Product B",
                    category = "Test",
                    imageUrl = "https://example.com/test.jpg",
                    price = 25.0,
                    stockQuantity = 5,
                    supplierId = "test"
                )
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

            if (testProductIds.size < 2) {
                addMessage("Failed to setup prerequisites for order test. Aborting order operations.")
                return@launch
            }

            // 1. Create and Insert an Order
            val order1 = OrderEntity(
                customerId = testCustomerId,
                status = "Pending",
                totalAmount = (1 * testProductPrices[testProductIds[0]]!!) + (2 * testProductPrices[testProductIds[1]]!!), // Calculated based on items below
                orderDate = System.currentTimeMillis()
            )

            val orderItem1 = OrderItemEntity(
                orderId = order1.orderId, // Will be set by insertOrderWithItems if not set here
                productId = testProductIds[0],
                quantity = 1,
                price = testProductPrices[testProductIds[0]]!!
            )
            val orderItem2 = OrderItemEntity(
                orderId = order1.orderId, // Will be set by insertOrderWithItems if not set here
                productId = testProductIds[1],
                quantity = 2,
                price = testProductPrices[testProductIds[1]]!!
            )

            appRepository.insertOrderWithItems(order1, listOf(orderItem1, orderItem2))
            addMessage("Inserted Order ID: ${order1.orderId} with 2 items using combined operation.")

            appRepository.getOrderWithOrderItems(order1.orderId).collect { orderWithItems ->
                if (orderWithItems != null) {
                    addMessage("Fetched Order with Items: ID=${orderWithItems.order.orderId}, CustID=${orderWithItems.order.customerId}, Status=${orderWithItems.order.status}, Total=${orderWithItems.order.totalAmount}, Date=${orderWithItems.order.orderDate}")
                    orderWithItems.items.forEach { item ->
                        addMessage("  Item: ProdID=${item.productId}, Qty=${item.quantity}, Price=${item.price}")
                    }
                } else {
                    addMessage("Order with ID ${order1.orderId} not found after insert.")
                }
            }

            appRepository.getAllOrdersWithOrderItems().collect { allOrdersWithItems ->
                addMessage("--- All Orders with Items (${allOrdersWithItems.size}) ---")
                allOrdersWithItems.forEach { orderWithItems ->
                    addMessage("Order: ID=${orderWithItems.order.orderId}, CustID=${orderWithItems.order.customerId}, Status=${orderWithItems.order.status}, Total=${orderWithItems.order.totalAmount}, Date=${orderWithItems.order.orderDate}")
                    orderWithItems.items.forEach { item ->
                        addMessage("  Item: ProdID=${item.productId}, Qty=${item.quantity}, Price=${item.price}")
                    }
                }
                addMessage("--- End of All Orders ---")
            }

            // Test Order Date Range Query
            val today = System.currentTimeMillis()
            val yesterday = today - (24 * 60 * 60 * 1000)
            val dayBeforeYesterday = yesterday - (24 * 60 * 60 * 1000)
            val tomorrow = today + (24 * 60 * 60 * 1000)

            appRepository.deleteAllOrderItemsForOrder(order1.orderId)
            appRepository.deleteOrder(order1)

            val orderYesterday = OrderEntity(customerId = testCustomerId, status = "Delivered", totalAmount = 50.0, orderDate = yesterday)
            val orderToday = OrderEntity(customerId = testCustomerId, status = "Processing", totalAmount = 75.0, orderDate = today)
            appRepository.insertOrder(orderYesterday)
            appRepository.insertOrder(orderToday)
            addMessage("Inserted order for yesterday (ID ${orderYesterday.orderId}) and today (ID ${orderToday.orderId}) for date range test.")

            appRepository.getOrdersByDateRange(dayBeforeYesterday, today).collect { dateRangeOrders ->
                addMessage("Orders in range D-2 to Today (${dateRangeOrders.size}):")
                dateRangeOrders.forEach { order -> addMessage("  Order ID: ${order.orderId}, Date: ${order.orderDate}, Status: ${order.status}")}
            }

            appRepository.getOrdersByDateRange(dayBeforeYesterday, yesterday).collect { dateRangeOrders ->
                addMessage("Orders in range D-2 to Yesterday (${dateRangeOrders.size}):")
                dateRangeOrders.forEach { order -> addMessage("  Order ID: ${order.orderId}, Date: ${order.orderDate}, Status: ${order.status}")}
            }

            appRepository.getOrdersByDateRange(tomorrow, tomorrow + (24*60*60*1000)).collect { dateRangeOrders ->
                addMessage("Orders in future range (${dateRangeOrders.size}):")
                dateRangeOrders.forEach { order -> addMessage("  Order ID: ${order.orderId}, Date: ${order.orderDate}, Status: ${order.status}")}
            }
        }
    }
}
