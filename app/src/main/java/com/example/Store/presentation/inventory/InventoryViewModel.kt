package com.example.Store.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Store.presentation.inventory.model.InventoryItemUi
import com.example.Store.presentation.inventory.model.InventoryTab
import com.example.Store.presentation.inventory.model.InventoryUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

open class InventoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    open val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadInventory()
    }

    private fun loadInventory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // simulate loading
            val mockItems = createMockItems()
            _uiState.update {
                it.copy(
                    items = mockItems,
                    filteredItems = mockItems,
                    isLoading = false
                )
            }
        }
    }

    fun onSearchChanged(query: String) {
        _uiState.update { it.copy(searchText = query) }
        filterInventory()
    }

    fun onTabSelected(tab: InventoryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        filterInventory()
    }

    private fun filterInventory() {
        val current = _uiState.value
        val filtered = current.items
            .filter { item ->
                val matchesText = item.name.contains(current.searchText, ignoreCase = true) ||
                        item.category.contains(current.searchText, ignoreCase = true)
                val matchesTab = when (current.selectedTab) {
                    InventoryTab.ALL -> true
                    InventoryTab.ENTRIES -> item.quantity >= 0
                    InventoryTab.EXITS -> item.quantity < 0
                }
                matchesText && matchesTab
            }

        _uiState.update { it.copy(filteredItems = filtered) }
    }

    // Placeholder for scanning
    fun scanBarcode() {
        _uiState.update { it.copy(userMessage = "Escaneo de código de barras (por implementar)") }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    // Simulated mock items
    private fun createMockItems(): List<InventoryItemUi> = listOf(
        InventoryItemUi("1", "Leche", 0, 1.5, "Lácteos", LocalDate.now().plusDays(2)),
        InventoryItemUi("2", "Pan", 4, 0.75, "Panadería", LocalDate.now().plusDays(5)),
        InventoryItemUi("3", "Agua", 10, 0.5, "Bebidas"),
        InventoryItemUi("4", "Queso", 2, 3.2, "Lácteos", LocalDate.now().plusDays(1)),
        InventoryItemUi("5", "Cereal", 6, 2.8, "Desayuno")
    )
}