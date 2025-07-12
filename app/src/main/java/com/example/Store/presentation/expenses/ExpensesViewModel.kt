package com.example.Store.presentation.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Store.presentation.expenses.model.ExpenseItemUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpensesUiState(
    val expenses: List<ExpenseItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val totalExpenses: Double = 0.0 // Calculated total
)

class ExpensesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000) // Simulate data loading
            val mockExpenses = createMockExpenses()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    expenses = mockExpenses,
                    totalExpenses = mockExpenses.sumOf { exp -> exp.amount }
                )
            }
        }
    }

    fun addExpensePlaceholder() {
        // In a real app, this would navigate to a new screen or show a detailed dialog
        _uiState.update {
            it.copy(userMessage = "Add New Expense action triggered (Placeholder).")
        }
    }

    fun viewExpenseDetailsPlaceholder(expenseId: String) {
        val expense = _uiState.value.expenses.find { it.id == expenseId }
        _uiState.update {
            it.copy(
                userMessage = expense?.let { e ->
                    "Viewing details for expense: '${e.description}' (Placeholder)."
                } ?: "Expense not found."
            )
        }
    }

    // Example of how a real add might work, including recalculating total
    fun addActualExpense(description: String, category: String, amount: Double, vendor: String? = null) {
        val newExpense = ExpenseItemUi(
            description = description,
            category = category,
            amount = amount,
            vendor = vendor
        )
        val updatedExpenses = _uiState.value.expenses + newExpense
        _uiState.update {
            it.copy(
                expenses = updatedExpenses.sortedByDescending { exp -> exp.expenseDate },
                totalExpenses = updatedExpenses.sumOf { exp -> exp.amount },
                userMessage = "'${newExpense.description}' added to expenses."
            )
        }
    }


    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun createMockExpenses(): List<ExpenseItemUi> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            ExpenseItemUi(
                description = "Office Rent - October",
                category = "Rent",
                amount = 1200.00,
                expenseDate = currentTime - (5 * 24 * 60 * 60 * 1000), // 5 days ago
                vendor = "City Properties LLC"
            ),
            ExpenseItemUi(
                description = "Electricity Bill",
                category = "Utilities",
                amount = 150.75,
                expenseDate = currentTime - (2 * 24 * 60 * 60 * 1000), // 2 days ago
                vendor = "Power Grid Corp"
            ),
            ExpenseItemUi(
                description = "Internet Services",
                category = "Utilities",
                amount = 79.99,
                expenseDate = currentTime - (1 * 24 * 60 * 60 * 1000), // 1 day ago
                vendor = "ConnectFast ISP"
            ),
            ExpenseItemUi(
                description = "Stationery Supplies",
                category = "Supplies",
                amount = 45.50,
                expenseDate = currentTime,
                vendor = "Office Depot"
            ),
            ExpenseItemUi(
                description = "Marketing Campaign - Q4",
                category = "Marketing",
                amount = 500.00,
                expenseDate = currentTime - (10 * 24 * 60 * 60 * 1000) // 10 days ago
            )
        ).sortedByDescending { it.expenseDate }
    }
}
