package com.example.store.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.presentation.orders.model.OrderItemUi
import com.example.store.presentation.orders.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrdersUiState(
    val orders: List<OrderItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

class OrdersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000) // Simulate data loading
            _uiState.update {
                it.copy(
                    isLoading = false,
                    orders = createMockOrders()
                )
            }
        }
    }

    fun createNewOrderPlaceholder() {
        _uiState.update {
            it.copy(userMessage = "Create New Order action triggered (Placeholder).")
        }
    }

    fun viewOrderDetailsPlaceholder(orderId: String) {
        val order = _uiState.value.orders.find { it.id == orderId }
        _uiState.update {
            it.copy(
                userMessage = order?.let { o ->
                    "Viewing details for order ${o.orderNumber} (Placeholder)."
                } ?: "Order not found."
            )
        }
    }

    fun updateOrderStatusPlaceholder(orderId: String, newStatus: OrderStatus) {
        // In a real app, this would also update the backend.
        // For now, we'll just update the local list and show a message.
        val currentOrders = _uiState.value.orders
        val updatedOrders = currentOrders.map { order ->
            if (order.id == orderId) {
                order.copy(status = newStatus)
            } else {
                order
            }
        }
        if (currentOrders != updatedOrders) {
             _uiState.update {
                it.copy(
                    orders = updatedOrders,
                    userMessage = "Order ${updatedOrders.find{it.id == orderId}?.orderNumber} status updated to ${newStatus.getDisplayValue()} (Placeholder)."
                )
            }
        } else {
             _uiState.update {
                it.copy(userMessage = "Failed to update status for order ID $orderId (not found).")
            }
        }
    }


    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun createMockOrders(): List<OrderItemUi> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            OrderItemUi(
                customerName = "Alice Wonderland",
                orderDate = currentTime - (3 * 24 * 60 * 60 * 1000), // 3 days ago
                status = OrderStatus.DELIVERED,
                totalAmount = 75.50,
                itemSummary = "Books (2), Tea Set (1)"
            ),
            OrderItemUi(
                customerName = "Bob The Builder",
                orderDate = currentTime - (1 * 24 * 60 * 60 * 1000), // 1 day ago
                status = OrderStatus.SHIPPED,
                totalAmount = 120.00,
                itemSummary = "Toolbox (1), Hammer (5), Nails (box)"
            ),
            OrderItemUi(
                customerName = "Charlie Brown",
                orderDate = currentTime - (2 * 60 * 60 * 1000), // 2 hours ago
                status = OrderStatus.PROCESSING,
                totalAmount = 30.25,
                itemSummary = "Kite (1), Comic Book (3)"
            ),
            OrderItemUi(
                customerName = "Diana Prince",
                orderDate = currentTime, // Just now
                status = OrderStatus.PENDING,
                totalAmount = 250.00,
                itemSummary = "Invisible Jet Parts (3), Lasso (1)"
            ),
             OrderItemUi(
                customerName = "Edward Scissorhands",
                orderDate = currentTime - (5 * 24 * 60 * 60 * 1000), // 5 days ago
                status = OrderStatus.CANCELED,
                totalAmount = 55.75,
                itemSummary = "Gloves (10 pairs) - Canceled"
            )
        ).sortedByDescending { it.orderDate }
    }
}
