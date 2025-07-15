package com.example.store.presentation.location

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.store.domain.usecase.location.CreateLocationUseCase
import com.example.store.domain.usecase.location.GetLocationByIdUseCase
import com.example.store.domain.usecase.location.UpdateLocationUseCase
import com.example.store.data.local.entity.LocationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.UUID
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationDetailUiState(
    val locationName: String = "",
    val address: String = "",
    val capacity: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isNewLocation: Boolean = true,
    val error: String? = null,
    val isSaveSuccess: Boolean = false
)

@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val createLocationUseCase: CreateLocationUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val locationId: String? = savedStateHandle["locationId"]

    private val _uiState = MutableStateFlow(LocationDetailUiState())
    val uiState: StateFlow<LocationDetailUiState> = _uiState.asStateFlow()

    init {
        if (locationId != null) {
            _uiState.update { it.copy(isNewLocation = false, isLoading = true) }
            viewModelScope.launch {
                getLocationByIdUseCase(locationId).filterNotNull().first().let { location ->
                    _uiState.update {
                        it.copy(
                            locationName = location.name,
                            address = location.address ?: "",
                            capacity = location.capacity?.toString() ?: "",
                            notes = location.notes ?: "",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(locationName = name) }
    }

    fun onAddressChange(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    fun onCapacityChange(capacity: String) {
        _uiState.update { it.copy(capacity = capacity) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun saveLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val location = LocationEntity(
                    locationId = locationId ?: UUID.randomUUID().toString(),
                    name = uiState.value.locationName,
                    address = uiState.value.address.takeIf { it.isNotBlank() },
                    capacity = uiState.value.capacity.toDoubleOrNull(),
                    notes = uiState.value.notes.takeIf { it.isNotBlank() }
                )

                if (uiState.value.isNewLocation) {
                    createLocationUseCase(location)
                } else {
                    updateLocationUseCase(location)
                }
                _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
