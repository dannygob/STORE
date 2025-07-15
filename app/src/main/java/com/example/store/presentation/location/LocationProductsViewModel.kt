package com.example.store.presentation.location

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.data.local.entity.LocationEntity
import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.domain.usecase.location.GetLocationByIdUseCase
import com.example.store.domain.usecase.productlocation.GetProductsAtLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class LocationProductsUiState(
    val location: LocationEntity? = null,
    val products: List<ProductLocationEntity> = emptyList(),
    val isLoading: Boolean = false
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
