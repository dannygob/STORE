package com.example.Store.presentation.orders


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Store.data.local.entity.OrderEntity
import com.example.Store.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    val orders: StateFlow<List<OrderEntity>> = appRepository.getAllOrders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}