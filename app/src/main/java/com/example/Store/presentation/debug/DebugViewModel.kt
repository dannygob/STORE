package com.example.Store.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Store.data.local.entity.CustomerEntity
import com.example.Store.data.local.entity.OrderEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.StockAtWarehouseEntity
import com.example.Store.data.local.entity.SupplierEntity
import com.example.Store.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _debugMessages = MutableStateFlow<List<String>>(emptyList())
    val debugMessages: StateFlow<List<String>> = _debugMessages.asStateFlow()

    init {
        addMessage("DebugViewModel Initialized.")
        testCoreDatabaseOperations()
        testOrderOperations()
        testUserPreferenceOperations()
        testWarehouseOperations()
        testStockAtWarehouseOperations()
    }

    private fun addMessage(message: String) {
        _debugMessages.value = _debugMessages.value + message
    }

    fun testCoreDatabaseOperations() {
        viewModelScope.launch {
            addMessage("Starting core database operations...")

        }
    }
}
