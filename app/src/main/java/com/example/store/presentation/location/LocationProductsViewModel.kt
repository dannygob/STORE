package com.example.store.presentation.location


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.domain.model.Location
import com.example.store.domain.model.ProductLocation
import com.example.store.domain.usecase.location.GetLocationByIdUseCase
import com.example.store.domain.usecase.productlocation.GetProductsAtLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class LocationProductsUiState(
    val location: Location? = null,
    val products: List<ProductLocation> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class LocationProductsViewModel @Inject constructor(
    private val getProductsAtLocationUseCase: GetProductsAtLocationUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val locationId: String = savedStateHandle.get<String>("locationId")!!

    private val _uiState = MutableStateFlow(LocationProductsUiState(isLoading = true))
    val uiState: StateFlow<LocationProductsUiState> = _uiState.asStateFlow()

    init {
        val locationFlow = getLocationByIdUseCase(locationId)
        val productsFlow = getProductsAtLocationUseCase(locationId)

        combine(locationFlow, productsFlow) { location, products ->
            LocationProductsUiState(location = location, products = products)
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }
}
