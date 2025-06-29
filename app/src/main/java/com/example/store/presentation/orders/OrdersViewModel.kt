package com.example.store.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.presentation.orders.model.CartItem
import com.example.store.presentation.orders.model.ProductUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class OrdersUiState(
    val products: List<ProductUi> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val selectedCustomer: String? = null,
    val customerSuggestions: List<String> = emptyList(),
    val showConfirmation: Boolean = false,
    val isDragInProgress: Boolean = false,
    val userMessage: String? = null,
    val lastOrderId: Int = 0,
)

class OrdersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val allCustomers =
        listOf("Alice Wonderland", "Bob The Builder", "Charlie Brown", "Diana Prince")

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    products = createMockProducts()
                )
            }
        }
    }

    fun onProductDragged(product: ProductUi) {
        _uiState.update { it.copy(isDragInProgress = true) }
    }

    fun addToCart(product: ProductUi) {
        _uiState.update { currentState ->
            val cart = currentState.cartItems.toMutableList()
            val existingItem = cart.find { it.product.id == product.id }

            if (product.stock > 0) {
                if (existingItem != null) {
                    existingItem.quantity++
                } else {
                    cart.add(CartItem(product = product, quantity = 1))
                }
                product.stock--
                currentState.copy(cartItems = cart, isDragInProgress = false)
            } else {
                currentState.copy(
                    userMessage = "No stock for ${product.name}",
                    isDragInProgress = false
                )
            }
        }
    }

    fun incrementCartItem(productId: String) {
        _uiState.update { currentState ->
            val cart = currentState.cartItems.toMutableList()
            val item = cart.find { it.product.id == productId }
            if (item != null && item.product.stock > 0) {
                item.quantity++
                item.product.stock--
            }
            currentState.copy(cartItems = cart)
        }
    }

    fun decrementCartItem(productId: String) {
        _uiState.update { currentState ->
            val cart = currentState.cartItems.toMutableList()
            val item = cart.find { it.product.id == productId }
            if (item != null) {
                item.quantity--
                item.product.stock++
                if (item.quantity == 0) {
                    cart.remove(item)
                }
            }
            currentState.copy(cartItems = cart)
        }
    }

    fun onCustomerInputChanged(input: String) {
        _uiState.update {
            it.copy(
                selectedCustomer = input,
                customerSuggestions = if (input.isNotBlank()) {
                    allCustomers.filter { name -> name.contains(input, ignoreCase = true) }
                } else {
                    emptyList()
                }
            )
        }
    }

    fun selectCustomer(name: String) {
        _uiState.update { it.copy(selectedCustomer = name, customerSuggestions = emptyList()) }
    }

    fun promptOrderConfirmation() {
        _uiState.update { it.copy(showConfirmation = true) }
    }

    fun cancelConfirmation() {
        _uiState.update { it.copy(showConfirmation = false) }
    }

    fun confirmOrder() {
        _uiState.update { currentState ->
            val customerName = currentState.selectedCustomer
            if (customerName.isNullOrBlank()) {
                return@update currentState.copy(userMessage = "Please select or register a customer.")
            }

            val newOrderId = currentState.lastOrderId + 1
            val orderCode = generateOrderCode(customerName, newOrderId)

            // Placeholder for saving the order
            println("Order Confirmed: $orderCode, Customer: $customerName, Items: ${currentState.cartItems}")

            currentState.copy(
                cartItems = emptyList(),
                selectedCustomer = null,
                showConfirmation = false,
                userMessage = "Order $orderCode generated successfully!",
                lastOrderId = newOrderId
            )
        }
    }

    private fun generateOrderCode(customerName: String, orderId: Int): String {
        val initials = customerName.take(3).uppercase(Locale.getDefault())
        return "$initials${String.format("%04d", orderId)}"
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun createMockProducts(): List<ProductUi> {
        return listOf(
            ProductUi(name = "Laptop Pro", stock = 10, price = 1200.00),
            ProductUi(name = "Smartphone X", stock = 25, price = 800.00),
            ProductUi(name = "Wireless Mouse", stock = 50, price = 25.00),
            ProductUi(name = "Keyboard", stock = 30, price = 45.00),
            ProductUi(name = "USB-C Hub", stock = 40, price = 35.00),
            ProductUi(name = "Monitor 27\"", stock = 15, price = 300.00)
        )
    }
}
