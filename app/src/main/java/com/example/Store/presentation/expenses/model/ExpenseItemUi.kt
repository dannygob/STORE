package com.example.Store.presentation.expenses.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// Consider making categories an enum if they are predefined and fixed
// For now, a string is flexible for mock data.
// enum class ExpenseCategory { RENT, UTILITIES, SUPPLIES, SALARIES, MARKETING, MISCELLANEOUS }

data class ExpenseItemUi(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val category: String, // Could be ExpenseCategory enum later
    val amount: Double,
    val expenseDate: Long = System.currentTimeMillis(),
    val vendor: String? = null // Optional: e.g., utility company, landlord
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Simpler date for expenses
        return sdf.format(Date(expenseDate))
    }

    fun getFormattedAmount(): String {
        return String.format(Locale.US, "$%.2f", amount)
    }
}
