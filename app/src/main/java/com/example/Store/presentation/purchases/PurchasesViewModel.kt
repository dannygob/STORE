package com.example.Store.presentation.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Store.presentation.purchases.model.PurchaseItemUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PurchasesUiState(
    val purchases: List<PurchaseItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val inventory: List<PurchaseItemUi> = emptyList(),
    val cart: List<PurchaseItemUi> = emptyList(), // Placeholder for cart items
    val customerSearchQuery: String = "",
    val newCustomerName: String = "",
    val customers: List<String> = emptyList(),
    val selectedCustomer: String? = null,
)

class PurchasesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PurchasesUiState())
    val uiState: StateFlow<PurchasesUiState> = _uiState.asStateFlow()

    init {
        loadPurchaseHistory()
    }

    private fun loadPurchaseHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000) // Simulate data loading
            _uiState.update {
                it.copy(
                    isLoading = false,
                    purchases = createMockPurchases()
                )
            }
        }
    }

    fun recordNewPurchasePlaceholder() {
        // In a real app, this would likely navigate to a new screen or show a detailed dialog
        // For now, just a message.
        _uiState.update {
            it.copy(userMessage = "Record New Purchase action triggered (Placeholder).")
        }
    }

    fun viewPurchaseDetailsPlaceholder(purchaseId: String) {
        val purchase = _uiState.value.purchases.find { it.id == purchaseId }
        _uiState.update {
            it.copy(
                userMessage = purchase?.let { p ->
                    "Viewing details for purchase of '${p.productName}' (Placeholder)."
                } ?: "Purchase not found."
            )
        }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun createMockPurchases(): List<PurchaseItemUi> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            PurchaseItemUi(
                productName = "Apples (Box)",
                supplierName = "Fresh Farms Inc.",
                quantity = 5,
                unitPrice = 10.0, // Price per box
                purchaseDate = currentTime - (2 * 24 * 60 * 60 * 1000) // 2 days ago
            ),
            PurchaseItemUi(
                productName = "Milk Cartons (Case)",
                supplierName = "Dairy Best",
                quantity = 10,
                unitPrice = 12.5, // Price per case
                purchaseDate = currentTime - (1 * 24 * 60 * 60 * 1000) // 1 day ago
            ),
            PurchaseItemUi(
                productName = "Printer Paper (Ream)",
                supplierName = "Office Supplies Co.",
                quantity = 20,
                unitPrice = 4.0, // Price per ream
                purchaseDate = currentTime
            ),
            PurchaseItemUi(
                productName = "Cleaning Solution (Bottle)",
                supplierName = "CleanPro Ltd.",
                quantity = 10,
                unitPrice = 3.5,
                purchaseDate = currentTime - (5 * 24 * 60 * 60 * 1000) // 5 days ago
            )
        ).sortedByDescending { it.purchaseDate }
    }
}
