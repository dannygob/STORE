package com.example.store.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.presentation.dashboard.model.NotificationItemUi
import com.example.store.presentation.dashboard.model.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val notifications: List<NotificationItemUi> = emptyList(),
    val unreadNotificationCount: Int = 0,
    val isLoadingNotifications: Boolean = false,
    val userMessage: String? = null // For general messages/errors from VM actions
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingNotifications = true) }
            kotlinx.coroutines.delay(1000) // Simulate network/db delay
            val mockNotifications = createMockNotifications()
            _uiState.update {
                it.copy(
                    isLoadingNotifications = false,
                    notifications = mockNotifications,
                    unreadNotificationCount = mockNotifications.count { n -> !n.isRead }
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
            NotificationItemUi(title = "New Order Received!", message = "Order #ORD-12345 has been placed for 3 items.", type = NotificationType.ORDER_NEW, timestamp = currentTime - (5 * 60 * 1000)), // 5 mins ago
            NotificationItemUi(title = "Low Stock Warning", message = "Apples are running low. Only 5 left.", type = NotificationType.LOW_STOCK, timestamp = currentTime - (1 * 60 * 60 * 1000), isRead = true), // 1 hour ago
            NotificationItemUi(title = "Order Delivered", message = "Order #ORD-12300 has been successfully delivered to John Doe.", type = NotificationType.ORDER_DELIVERED, timestamp = currentTime - (3 * 60 * 60 * 1000)), // 3 hours ago
            NotificationItemUi(title = "Item Expiring Soon", message = "Milk (Batch #M45) will expire in 2 days.", type = NotificationType.ITEM_EXPIRED, timestamp = currentTime - (24 * 60 * 60 * 1000)), // 1 day ago
            NotificationItemUi(title = "System Maintenance", message = "Scheduled maintenance tonight at 2 AM.", type = NotificationType.SYSTEM_ALERT, timestamp = currentTime - (2 * 24 * 60 * 60 * 1000), isRead = true) // 2 days ago
        ).sortedByDescending { it.timestamp }
    }
}
