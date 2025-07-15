package com.example.Store.presentation.inventory.ui


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Store.domain.usecase.productlocation.AssignProductToLocationUseCase
import com.example.Store.domain.usecase.productlocation.MoveProductStockUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StockManagementUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ProductStockManagementViewModel @Inject constructor(
    private val assignProductToLocationUseCase: AssignProductToLocationUseCase,
    private val moveProductStockUseCase: MoveProductStockUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val productId: String = savedStateHandle.get<String>("productId")!!

    private val _uiState = MutableStateFlow(StockManagementUiState())
    val uiState: StateFlow<StockManagementUiState> = _uiState.asStateFlow()

    fun assignToNewLocation(
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?,
        amount: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                assignProductToLocationUseCase(productId, locationId, aisle, shelf, level, amount)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun transferStock(fromLocationId: String, toLocationId: String, amount: Int) {
        // This is a simplified transfer. A real implementation would need to specify
        // the exact aisle/shelf/level for both source and destination.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Placeholder for a more complex transfer logic
                // moveProductStockUseCase(productId, fromLocationId, null,null,null, toLocationId, null,null,null, amount)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = "Transfer not fully implemented"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}