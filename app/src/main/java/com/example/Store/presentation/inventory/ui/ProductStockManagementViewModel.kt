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

    fun transferStock(
        fromLocationId: String, fromAisle: String?, fromShelf: String?, fromLevel: String?,
        toLocationId: String, toAisle: String?, toShelf: String?, toLevel: String?,
        amount: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                moveProductStockUseCase(
                    productId,
                    fromLocationId, fromAisle, fromShelf, fromLevel,
                    toLocationId, toAisle, toShelf, toLevel,
                    amount
                )
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}