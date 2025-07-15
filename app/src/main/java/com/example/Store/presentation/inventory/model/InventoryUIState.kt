package com.example.Store.presentation.inventory.model

import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.ProductLocationEntity


data class InventoryUiState(
    val items: List<InventoryItemUi> = emptyList(),
    val filteredItems: List<InventoryItemUi> = emptyList(),
    val searchText: String = "",
    val selectedTab: InventoryTab = InventoryTab.ALL,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    // Fields for master-detail view
    val selectedProduct: ProductEntity? = null,
    val productLocations: List<ProductLocationEntity> = emptyList()
)