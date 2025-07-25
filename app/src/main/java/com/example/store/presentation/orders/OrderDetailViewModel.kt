package com.example.store.presentation.orders


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.data.local.dao.OrderWithOrderItems
import com.example.store.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class OrderDetailUiState(
    val orderWithItems: OrderWithOrderItems? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val appRepository: AppRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: String = savedStateHandle.get<String>("orderId")!!

    val uiState: StateFlow<OrderDetailUiState> = appRepository.getOrderWithOrderItems(orderId)
        .map { OrderDetailUiState(orderWithItems = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OrderDetailUiState(isLoading = true)
        )
}
