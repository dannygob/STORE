package com.example.store.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.domain.repository.AuthRepository
import com.example.store.presentation.dashboard.model.NotificationItemUi
import com.example.store.presentation.dashboard.model.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val notifications: List<NotificationItemUi> = emptyList(), // All notifications for the panel
    val unreadNotificationCount: Int = 0,
    val isLoadingNotifications: Boolean = false,
    val userMessage: String? = null, // For general messages/errors from VM actions
    val lowStockItemCount: Int = 0,
    val expiringItemCount: Int = 0,
    val lowStockItemsList: List<NotificationItemUi> = emptyList(), // Specific list for low stock card
    val expiringItemsList: List<NotificationItemUi> = emptyList() // Specific list for expiring card
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin: SharedFlow<Unit> = _navigateToLogin

    init {
        loadNotifications()
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _navigateToLogin.emit(Unit)
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingNotifications = true) }
            kotlinx.coroutines.delay(1000) // Simulate network/db delay
            val mockNotifications = createMockNotifications()
            val lowStockItems = mockNotifications.filter { it.type == NotificationType.LOW_STOCK }
            val expiringItems = mockNotifications.filter { it.type == NotificationType.ITEM_EXPIRED }
            _uiState.update {
                it.copy(
                    isLoadingNotifications = false,
                    notifications = mockNotifications, // This is for the general notification panel
                    unreadNotificationCount = mockNotifications.count { n -> !n.isRead },
                    lowStockItemsList = lowStockItems,
                    expiringItemsList = expiringItems,
                    lowStockItemCount = lowStockItems.size,
                    expiringItemCount = expiringItems.size
                )
            }
        }
    }

    fun markAsRead(notificationId: String) {
        val updatedNotifications = _uiState.value.notifications.map {
            if (it.id == notificationId && !it.isRead) {
                it.copy(isRead = true)
            } else {
                it
            }
        }
        if (updatedNotifications != _uiState.value.notifications) {
            _uiState.update {
                it.copy(
                    notifications = updatedNotifications,
                    unreadNotificationCount = updatedNotifications.count { n -> !n.isRead }
                    // userMessage = "Notification marked as read." // Optional: could be too noisy
                )
            }
        }
    }

    fun markAllAsRead() {
        val allRead = _uiState.value.notifications.all { it.isRead }
        if (allRead) {
            _uiState.update { it.copy(userMessage = "All notifications already read.") }
            return
        }

        val updatedNotifications = _uiState.value.notifications.map {
            it.copy(isRead = true)
        }
        _uiState.update {
            it.copy(
                notifications = updatedNotifications,
                unreadNotificationCount = 0,
                userMessage = "All notifications marked as read."
            )
        }
    }

    fun dismissNotification(notificationId: String) {
        val notificationToDismiss = _uiState.value.notifications.find { it.id == notificationId }
        if (notificationToDismiss != null) {
            val updatedNotifications = _uiState.value.notifications.filterNot { it.id == notificationId }
            _uiState.update {
                it.copy(
                    notifications = updatedNotifications,
                    unreadNotificationCount = updatedNotifications.count { n -> !n.isRead },
                    userMessage = "Notification '${notificationToDismiss.title}' dismissed."
                )
            }
        }
    }

    fun dismissAllNotifications() {
        if (_uiState.value.notifications.isEmpty()){
            _uiState.update { it.copy(userMessage = "No notifications to dismiss.") }
            return
        }
        _uiState.update {
            it.copy(
                notifications = emptyList(),
                unreadNotificationCount = 0,
                userMessage = "All notifications dismissed."
            )
        }
    }

    fun refreshNotifications() {
        // Placeholder for actual refresh logic
        _uiState.update { it.copy(userMessage = "Refreshing notifications... (Placeholder)")}
        loadNotifications() // For now, just reload mock data
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun createMockNotifications(): List<NotificationItemUi> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            NotificationItemUi(id="1", title = "New Order Received!", message = "Order #ORD-12345 for 3 items.", type = NotificationType.ORDER_NEW, timestamp = currentTime - (5 * 60 * 1000)),

            // Multiple Low Stock Items
            NotificationItemUi(id="LS1", title = "Low Stock", message = "Red Apples (5 left)", type = NotificationType.LOW_STOCK, timestamp = currentTime - (1 * 60 * 60 * 1000), isRead = true),
            NotificationItemUi(id="LS2", title = "Low Stock", message = "Whole Milk (3 left)", type = NotificationType.LOW_STOCK, timestamp = currentTime - (2 * 60 * 60 * 1000)),
            NotificationItemUi(id="LS3", title = "Low Stock", message = "Brown Bread (2 left)", type = NotificationType.LOW_STOCK, timestamp = currentTime - (3 * 60 * 60 * 1000)),

            NotificationItemUi(id="2", title = "Order Delivered", message = "Order #ORD-12300 to John Doe.", type = NotificationType.ORDER_DELIVERED, timestamp = currentTime - (4 * 60 * 60 * 1000)),

            // Multiple Expiring Items
            NotificationItemUi(id="EXP1", title = "Expiring Soon", message = "Yogurt (Batch #YG7) - 1 day", type = NotificationType.ITEM_EXPIRED, timestamp = currentTime - (24 * 60 * 60 * 1000)),
            NotificationItemUi(id="EXP2", title = "Expiring Soon", message = "Cheese Slices (Batch #CS2) - 2 days", type = NotificationType.ITEM_EXPIRED, timestamp = currentTime - (2 * 24 * 60 * 60 * 1000), isRead = true),
            NotificationItemUi(id="EXP3", title = "Expiring Soon", message = "Orange Juice (Batch #OJ1) - 3 days", type = NotificationType.ITEM_EXPIRED, timestamp = currentTime - (3 * 24 * 60 * 60 * 1000)),
            NotificationItemUi(id="EXP4", title = "Expired Today", message = "Salad Mix (Batch #SM5)", type = NotificationType.ITEM_EXPIRED, timestamp = currentTime - (4 * 24 * 60 * 60 * 1000)),

            NotificationItemUi(id="3", title = "System Maintenance", message = "Tonight at 2 AM.", type = NotificationType.SYSTEM_ALERT, timestamp = currentTime - (5 * 24 * 60 * 60 * 1000), isRead = true)
        ).sortedByDescending { it.timestamp }
    }
}
