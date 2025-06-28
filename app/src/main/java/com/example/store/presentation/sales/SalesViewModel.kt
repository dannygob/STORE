package com.example.store.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

// --- Data Models ---

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    var stock: Int,
    val imageUrl: String? = null, // Placeholder for product image
)

data class Customer(
    val id: String,
    val name: String,
)

data class CartItem(
    val product: Product,
    var quantity: Int,
)

data class Order(
    val orderId: String,
    val customer: Customer,
    val items: List<CartItem>,
    val total: Double,
    val date: Long = System.currentTimeMillis(),
)

// --- UI State ---

data class SalesUiState(
    val inventory: List<Product> = emptyList(),
    val customers: List<Customer> = emptyList(),
    val cart: List<CartItem> = emptyList(),
    val selectedCustomer: Customer? = null,
    val customerSearchQuery: String = "",
    val newCustomerName: String = "",
    val lastOrderId: Int = 0,
    val userMessage: String? = null,
    val isLoading: Boolean = false,
    val showConfirmDialog: Boolean = false,
) {
    val cartTotal: Double
        get() = cart.sumOf { it.product.price * it.quantity }
}

// --- ViewModel ---

class SalesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate loading inventory and customers
            kotlinx.coroutines.delay(500)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    inventory = createMockInventory(),
                    customers = createMockCustomers()
                )
            }
        }
    }

    fun onProductDraggedToCart(productId: String) {
        val product = _uiState.value.inventory.find { it.id == productId }
        product?.let {
            if (it.stock > 0) {
                addProductToCart(it)
            } else {
                _uiState.update { state -> state.copy(userMessage = "Producto sin stock") }
            }
        }
    }

    private fun addProductToCart(product: Product) {
        _uiState.update { state ->
            val updatedInventory = state.inventory.map {
                if (it.id == product.id) it.copy(stock = it.stock - 1) else it
            }
            val cart = state.cart.toMutableList()
            val existingCartItem = cart.find { it.product.id == product.id }
            if (existingCartItem != null) {
                existingCartItem.quantity++
            } else {
                cart.add(CartItem(product = product, quantity = 1))
            }
            state.copy(inventory = updatedInventory, cart = cart)
        }
    }

    fun incrementCartItem(productId: String) {
        val product = _uiState.value.inventory.find { it.id == productId }
        product?.let {
            if (it.stock > 0) {
                addProductToCart(it)
            } else {
                _uiState.update { state -> state.copy(userMessage = "No hay más stock") }
            }
        }
    }

    fun decrementCartItem(productId: String) {
        _uiState.update { state ->
            val cart = state.cart.toMutableList()
            val cartItem = cart.find { it.product.id == productId }
            if (cartItem != null) {
                val updatedInventory = state.inventory.map {
                    if (it.id == productId) it.copy(stock = it.stock + 1) else it
                }
                if (cartItem.quantity > 1) {
                    cartItem.quantity--
                } else {
                    cart.remove(cartItem)
                }
                state.copy(inventory = updatedInventory, cart = cart)
            } else {
                state
            }
        }
    }

    fun onCustomerSearchChanged(query: String) {
        _uiState.update { it.copy(customerSearchQuery = query) }
    }

    fun onNewCustomerNameChanged(name: String) {
        _uiState.update { it.copy(newCustomerName = name) }
    }

    fun selectCustomer(customer: Customer) {
        _uiState.update {
            it.copy(
                selectedCustomer = customer,
                customerSearchQuery = customer.name
            )
        }
    }

    fun registerNewCustomer() {
        val name = _uiState.value.newCustomerName.trim()
        if (name.isNotBlank()) {
            val newCustomer = Customer(id = UUID.randomUUID().toString(), name = name)
            _uiState.update { state ->
                state.copy(
                    customers = state.customers + newCustomer,
                    selectedCustomer = newCustomer,
                    customerSearchQuery = name,
                    newCustomerName = ""
                )
            }
        }
    }

    fun onGenerateOrderClicked() {
        if (_uiState.value.cart.isEmpty()) {
            _uiState.update { it.copy(userMessage = "El carrito está vacío") }
            return
        }
        if (_uiState.value.selectedCustomer == null) {
            _uiState.update { it.copy(userMessage = "Seleccione o registre un cliente") }
            return
        }
        _uiState.update { it.copy(showConfirmDialog = true) }
    }

    fun confirmOrderGeneration() {
        val state = _uiState.value
        val customer = state.selectedCustomer!!
        val newOrderId = state.lastOrderId + 1
        val orderId = generateOrderId(customer.name, newOrderId)

        // Here you would typically save the order to a database
        // For now, we just show a message and reset the state
        val order = Order(
            orderId = orderId,
            customer = customer,
            items = state.cart,
            total = state.cartTotal
        )

        _uiState.update {
            it.copy(
                userMessage = "Orden ${order.orderId} generada!",
                cart = emptyList(),
                selectedCustomer = null,
                customerSearchQuery = "",
                lastOrderId = newOrderId,
                showConfirmDialog = false
            )
        }
    }

    fun dismissConfirmDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun generateOrderId(customerName: String, sequentialId: Int): String {
        val initials = customerName.take(3).uppercase(Locale.ROOT)
        val number = String.format("%04d", sequentialId)
        return "$initials$number"
    }

    // --- Mock Data ---
    private fun createMockInventory(): List<Product> {
        return listOf(
            Product("1", "Leche Entera", 1.25, 10),
            Product("2", "Arroz", 0.85, 20),
            Product("3", "Huevos", 0.25, 30),
            Product("4", "Pan", 2.0, 15),
            Product("5", "Pollo", 8.0, 5),
            Product("6", "Manzanas", 0.5, 50)
        )
    }

    private fun createMockCustomers(): List<Customer> {
        return listOf(
            Customer("101", "John Doe"),
            Customer("102", "Jane Smith"),
            Customer("103", "Peter Jones")
        )
    }
}
