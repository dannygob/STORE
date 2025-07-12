package com.example.Store.presentation.scanner

import androidx.lifecycle.ViewModel
import com.example.Store.presentation.scanner.model.ScannedDataUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ScannerUiState(
    val lastScannedItem: ScannedDataUi? = null,
    val scanHistory: List<ScannedDataUi> = emptyList(),
    val isScanningActive: Boolean = false, // To potentially change UI while "scanning"
    val userMessage: String? = null
)

class ScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    // Maximum items to keep in history
    private val maxHistorySize = 10

    fun startScan() {
        // In a real app, this would trigger camera permission checks and launch a scanner intent/library.
        // For now, it's a placeholder.
        _uiState.update {
            it.copy(
                isScanningActive = true, // Potentially useful for UI changes
                userMessage = "Scan started... (Point camera at a code - Placeholder)"
            )
        }
        // Simulate a scan happening after a short delay for placeholder behavior
        // viewModelScope.launch {
        //     kotlinx.coroutines.delay(2000) // Simulate time taken to scan
        //     if (_uiState.value.isScanningActive) { // Check if still "scanning"
        //         processScannedCode("SIMULATED-CODE-${(1000..9999).random()}")
        //     }
        // }
    }

    fun processScannedCode(code: String) {
        val newScan = ScannedDataUi(content = code)
        val updatedHistory = (_uiState.value.scanHistory + newScan).takeLast(maxHistorySize)

        _uiState.update {
            it.copy(
                lastScannedItem = newScan,
                scanHistory = updatedHistory,
                isScanningActive = false, // Turn off "scanning" state
                userMessage = "Scanned: $code"
            )
        }
    }

    fun cancelScan() {
        // If the user cancels the scanning process
        if (_uiState.value.isScanningActive) {
            _uiState.update {
                it.copy(
                    isScanningActive = false,
                    userMessage = "Scan cancelled."
                )
            }
        }
    }

    fun clearLastScan() {
        if (_uiState.value.lastScannedItem != null) {
            _uiState.update {
                it.copy(
                    lastScannedItem = null,
                    userMessage = "Last scan cleared."
                )
            }
        } else {
            _uiState.update {
                it.copy(userMessage = "Nothing to clear.")
            }
        }
    }

    fun clearScanHistory() {
        if (_uiState.value.scanHistory.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    scanHistory = emptyList(),
                    lastScannedItem = null, // Often you'd clear the last scan too if history is wiped
                    userMessage = "Scan history cleared."
                )
            }
        } else {
             _uiState.update {
                it.copy(userMessage = "Scan history is already empty.")
            }
        }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
