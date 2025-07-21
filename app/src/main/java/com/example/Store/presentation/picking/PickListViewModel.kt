package com.example.Store.presentation.picking

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.Store.domain.usecase.inventory.PickListItem

data class PickListUiState(
    val isLoading: Boolean = false,
    val pickList: List<PickListItem> = emptyList(),
    val error: String? = null
)
@HiltViewModel
class PickListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PickListUiState())
    val uiState: StateFlow<PickListUiState> = _uiState
    // TODO: Implement picking logic
}
