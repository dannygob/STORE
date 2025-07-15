package com.example.store.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.presentation.orders.model.CartItem
import com.example.store.presentation.orders.model.ProductUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

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

import com.example.store.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

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
        viewModelScope.launch {
            val currentState = _uiState.value
            val customerId = currentState.selectedCustomer // Assuming this is the customer ID for now
            if (customerId.isNullOrBlank()) {
                _uiState.update { it.copy(userMessage = "Please select a customer.") }
                return@launch
            }

            val newOrderId = UUID.randomUUID().toString()
            val order = OrderEntity(
                orderId = newOrderId,
                customerId = customerId,
                date = System.currentTimeMillis(),
                total = currentState.cartItems.sumOf { it.product.price * it.quantity }
            )

            val orderItems = currentState.cartItems.map { cartItem ->
                OrderItemEntity(
                    orderId = newOrderId,
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity,
                    unitPrice = cartItem.product.price
                )
            }

            try {
                appRepository.insertOrderWithItems(order, orderItems)
                _uiState.update {
                    it.copy(
                        cartItems = emptyList(),
                        selectedCustomer = null,
                        showConfirmation = false,
                        userMessage = "Order #$newOrderId generated successfully!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Error saving order: ${e.message}") }
            }
        }
    }

    private fun generateOrderCode(customerName: String, orderId: Int): String {
        val initials = customerName.take(3).uppercase(Locale.getDefault())
        return "$initials${String.format(Locale.US, "%04d", orderId)}"
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
