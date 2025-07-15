package com.example.store.presentation.picking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.domain.usecase.inventory.GeneratePickListUseCase
import com.example.store.domain.usecase.inventory.PickListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PickListUiState(
    val pickList: List<PickListItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PickListViewModel @Inject constructor(
    private val generatePickListUseCase: GeneratePickListUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: String = savedStateHandle.get<String>("orderId")!!

    private val _uiState = MutableStateFlow(PickListUiState(isLoading = true))
    val uiState: StateFlow<PickListUiState> = _uiState.asStateFlow()

    init {
        loadPickList()
    }

    private fun loadPickList() {
        viewModelScope.launch {
            try {
                generatePickListUseCase(orderId).collect { pickList ->
                    _uiState.update { it.copy(pickList = pickList, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
