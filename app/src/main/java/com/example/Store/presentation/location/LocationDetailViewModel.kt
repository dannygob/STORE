package com.example.Store.presentation.location

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Store.domain.model.Location
import com.example.Store.domain.usecase.location.CreateLocationUseCase
import com.example.Store.domain.usecase.location.GetLocationByIdUseCase
import com.example.Store.domain.usecase.location.UpdateLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class LocationDetailUiState(
    val location: Location? = null,
    val locationName: String = "",
    val address: String = "",
    val capacity: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val error: String? = null,
    val isNewLocation: Boolean = false
)

@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val createLocationUseCase: CreateLocationUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val locationId: String? = savedStateHandle.get<String>("locationId")

    private val _uiState = MutableStateFlow(LocationDetailUiState())
    val uiState: StateFlow<LocationDetailUiState> = _uiState.asStateFlow()

    init {
        if (locationId != null) {
            _uiState.value = _uiState.value.copy(isNewLocation = false)
            loadLocation()
        } else {
            _uiState.value = _uiState.value.copy(isNewLocation = true)
        }
    }

    private fun loadLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val location = getLocationByIdUseCase(locationId!!).first()
            if (location != null) {
                _uiState.value = _uiState.value.copy(
                    location = location,
                    locationName = location.name,
                    address = location.address ?: "",
                    capacity = location.capacity?.toString() ?: "",
                    notes = location.notes ?: "",
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(error = "Location not found", isLoading = false)
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(locationName = name)
    }

    fun onAddressChange(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
    }

    fun onCapacityChange(capacity: String) {
        _uiState.value = _uiState.value.copy(capacity = capacity)
    }

    fun onNotesChange(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun saveLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val location = Location(
                    locationId = locationId ?: UUID.randomUUID().toString(),
                    name = _uiState.value.locationName,
                    address = _uiState.value.address,
                    capacity = _uiState.value.capacity.toDoubleOrNull(),
                    notes = _uiState.value.notes
                )
                if (_uiState.value.isNewLocation) {
                    createLocationUseCase(location)
                } else {
                    updateLocationUseCase(location)
                }
                _uiState.value = _uiState.value.copy(isSaveSuccess = true, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}
