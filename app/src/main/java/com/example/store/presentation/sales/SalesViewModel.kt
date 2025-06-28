package com.example.store.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.presentation.sales.model.SaleItemUi
import com.example.store.presentation.sales.model.SoldItemSimple
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SalesUiState(
    val sales: List<SaleItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

class SalesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    init {
        loadSalesHistory()
    }

    private fun loadSalesHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000) // Simulate data loading
            _uiState.update {
                it.copy(
                    isLoading = false,
                    sales = createMockSales()
                )
            }
        }
    }

    fun recordNewSalePlaceholder() {
        _uiState.update {
            it.copy(userMessage = "Record New Sale action triggered (Placeholder).")
        }
    }

    fun viewSaleDetailsPlaceholder(saleId: String) {
        val sale = _uiState.value.sales.find { it.id == saleId }
        _uiState.update {
            it.copy(
                userMessage = sale?.let { s ->
                    "Viewing details for sale ${s.transactionId} (Placeholder)."
                } ?: "Sale not found."
            )
        }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun createMockSales(): List<SaleItemUi> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            SaleItemUi(
                itemsSoldSummary = "Apples (2), Milk (1)",
                items = listOf(
                    SoldItemSimple("Apples", 2, 0.5),
                    SoldItemSimple("Milk (1L)", 1, 1.2)
                ),
                totalAmount = (2 * 0.5) + (1 * 1.2),
                saleDate = currentTime - (1 * 24 * 60 * 60 * 1000), // 1 day ago
                customerName = "John Doe"
            ),
            SaleItemUi(
                itemsSoldSummary = "Bread (1), Eggs (1 dozen)",
                items = listOf(
                    SoldItemSimple("Bread Loaf", 1, 2.0),
                    SoldItemSimple("Eggs (dozen)", 1, 2.5)
                ),
                totalAmount = 2.0 + 2.5,
                saleDate = currentTime - (2 * 60 * 60 * 1000), // 2 hours ago
                customerName = "Jane Smith"
            ),
            SaleItemUi(
                itemsSoldSummary = "Chicken Breast (1kg)",
                items = listOf(
                    SoldItemSimple("Chicken Breast (kg)", 1, 8.0)
                ),
                totalAmount = 8.0,
                saleDate = currentTime,
                customerName = null // No customer name
            ),
            SaleItemUi(
                itemsSoldSummary = "Cereal Box (1), Bananas (3)",
                 items = listOf(
                    SoldItemSimple("Cereal Box", 1, 3.5),
                    SoldItemSimple("Bananas", 3, 0.3)
                ),
                totalAmount = 3.5 + (3*0.3),
                saleDate = currentTime - (3 * 24 * 60 * 60 * 1000) // 3 days ago
            )
        ).sortedByDescending { it.saleDate }
    }
}
