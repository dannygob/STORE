package com.example.store.presentation.inventory.model

data class InventoryUiState(
    val items: List<InventoryItemUi> = emptyList(),
    val filteredItems: List<InventoryItemUi> = emptyList(),
    val searchText: String = "",
    val selectedTab: InventoryTab = InventoryTab.ALL,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
)