package com.example.store.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.presentation.inventory.model.InventoryItemUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventoryUiState(
    val items: List<InventoryItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null // For Toasts or Snackbars
)

class InventoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadInventoryItems()
    }

    private fun loadInventoryItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate network/db delay
            kotlinx.coroutines.delay(1000)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    items = createMockInventoryItems()
                )
            }
        }
    }

    fun addItem(name: String, quantity: Int, price: Double) {
        // In a real app, this would involve a repository call
        val newItem = InventoryItemUi(name = name, quantity = quantity, price = price)
        _uiState.update {
            it.copy(
                items = it.items + newItem,
                userMessage = "Item '${newItem.name}' added."
            )
        }
    }

    fun deleteItem(itemId: String) {
        val itemToDelete = _uiState.value.items.find { it.id == itemId }
        _uiState.update {
            it.copy(
                items = it.items.filterNot { item -> item.id == itemId },
                userMessage = itemToDelete?.let { "Item '${it.name}' deleted." } ?: "Item not found."
            )
        }
    }

    fun editItemPlaceholder(itemId: String) {
        // This is a placeholder for future edit functionality
        val itemToEdit = _uiState.value.items.find { it.id == itemId }
        _uiState.update {
            it.copy(userMessage = itemToEdit?.let { "Edit action for '${it.name}' triggered." } ?: "Item not found for edit.")
        }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun createMockInventoryItems(): List<InventoryItemUi> {
        return listOf(
            InventoryItemUi(name = "Apples", quantity = 50, price = 0.5, category = "Fruits"),
            InventoryItemUi(name = "Bananas", quantity = 100, price = 0.3, category = "Fruits"),
            InventoryItemUi(name = "Milk (1L)", quantity = 20, price = 1.2, category = "Dairy"),
            InventoryItemUi(name = "Bread Loaf", quantity = 30, price = 2.0, category = "Bakery"),
            InventoryItemUi(name = "Chicken Breast (kg)", quantity = 15, price = 8.0, category = "Meat"),
            InventoryItemUi(name = "Cereal Box", quantity = 25, price = 3.5, category = "Pantry"),
            InventoryItemUi(name = "Eggs (dozen)", quantity = 40, price = 2.5, category = "Dairy")
        )
    }
}
