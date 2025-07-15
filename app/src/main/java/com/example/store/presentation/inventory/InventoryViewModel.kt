package com.example.store.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.data.repository.AppRepository
import com.example.store.domain.usecase.productlocation.GetLocationsForProductUseCase
import com.example.store.presentation.inventory.model.InventoryItemUi
import com.example.store.presentation.inventory.model.InventoryTab
import com.example.store.presentation.inventory.model.InventoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val getLocationsForProductUseCase: GetLocationsForProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            appRepository.getAllProducts()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { error -> _uiState.update { it.copy(isLoading = false, userMessage = error.message) } }
                .collect { products ->
                    val items = products.map {
                        InventoryItemUi(
                            id = it.id,
                            name = it.name,
                            quantity = it.stock,
                            price = it.salePrice,
                            category = it.category ?: "N/A"
                        )
                    }
                    _uiState.update {
                        it.copy(
                            items = items,
                            filteredItems = items, // Initially show all
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onProductSelected(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val product = appRepository.getProductById(productId).first()
            val locations = getLocationsForProductUseCase(productId).first()
            _uiState.update {
                it.copy(
                    selectedProduct = product,
                    productLocations = locations,
                    isLoading = false
                )
            }
        }
    }

    fun onDismissProductDetail() {
        _uiState.update { it.copy(selectedProduct = null, productLocations = emptyList()) }
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
                item.name.contains(current.searchText, ignoreCase = true) ||
                        item.category.contains(current.searchText, ignoreCase = true)
            }
        _uiState.update { it.copy(filteredItems = filtered) }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}