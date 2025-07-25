package com.example.store.presentation.warehouse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.store.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WarehouseUiState(
    val isLoading: Boolean = false,
    val orders: List<com.example.store.domain.model.OrderWithOrderItems> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class WarehouseViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WarehouseUiState())
    val uiState: StateFlow<WarehouseUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = WarehouseUiState(isLoading = true)
            try {
                repository.getAllOrdersWithOrderItems().collect { orders ->
                    _uiState.value = WarehouseUiState(orders = orders)
                }
            } catch (e: Exception) {
                _uiState.value = WarehouseUiState(error = e.message)
            }
        }
    }
}
