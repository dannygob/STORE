package com.example.Store.presentation.picking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Store.data.repository.AppRepository
import com.example.Store.domain.usecase.inventory.PickListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PickListUiState(
    val isLoading: Boolean = false,
    val pickList: List<PickListItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PickListViewModel @Inject constructor(
    private val generatePickListUseCase: GeneratePickListUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickListUiState())
    val uiState: StateFlow<PickListUiState> = _uiState.asStateFlow()

    private val orderId: String = savedStateHandle.get<String>("orderId")!!

    init {
        loadPickList()
    }

    private fun loadPickList() {
        viewModelScope.launch {
            _uiState.value = PickListUiState(isLoading = true)
            try {
                generatePickListUseCase(orderId).collect { pickList ->
                    _uiState.value = PickListUiState(pickList = pickList)
                }
            } catch (e: Exception) {
                _uiState.value = PickListUiState(error = e.message)
            }
        }
    }
}
