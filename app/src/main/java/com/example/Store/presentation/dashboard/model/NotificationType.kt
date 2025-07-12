package com.example.Store.presentation.dashboard.model

enum class NotificationType {
    ORDER_NEW,        // Bell icon
    ORDER_DELIVERED,  // Bell icon or a specific delivery icon
    LOW_STOCK,        // Warning triangle
    ITEM_EXPIRED,     // Warning triangle
    INFO,             // Generic info icon
    SYSTEM_ALERT      // Generic alert/warning icon
}
