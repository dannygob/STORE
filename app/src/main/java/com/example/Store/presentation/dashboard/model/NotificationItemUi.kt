package com.example.Store.presentation.dashboard.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class NotificationItemUi(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: NotificationType = NotificationType.INFO,
    var isRead: Boolean = false // Use var if directly mutable, or handle immutably in ViewModel
) {
    fun getFormattedTimestamp(): String {
        // More concise format for notifications, e.g., "10:30 AM" or "Yesterday" or "dd MMM"
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val oneMinute = 60 * 1000L
        val oneHour = 60 * oneMinute
        val oneDay = 24 * oneHour

        return when {
            diff < oneMinute -> "Just now"
            diff < oneHour -> "${diff / oneMinute}m ago"
            diff < oneDay -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
            diff < 2 * oneDay -> "Yesterday, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))}"
            else -> SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
