package com.example.store.presentation.scanner

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.store.presentation.scanner.model.ScannedDataUi
import com.example.store.presentation.scanner.ui.ScannerScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.mockito.kotlin.times

class ScannerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: ScannerViewModel
    private val mockUiState = MutableStateFlow(ScannerUiState())

    private val sampleScan1 = ScannedDataUi(content = "SCAN-001")
    private val sampleScan2 = ScannedDataUi(content = "SCAN-002")

    @Before
    fun setUp() {
        mockViewModel = mock<ScannerViewModel> {
            on { uiState } doReturn mockUiState
        }
        // Initial state for most tests
        mockUiState.value = ScannerUiState(lastScannedItem = null, scanHistory = emptyList())

        composeTestRule.setContent {
            ScannerScreen(viewModel = mockViewModel)
        }
    }

    @Test
    fun scannerScreen_initialState_displaysCorrectly() {
        composeTestRule.onNodeWithText("Barcode Scanner").assertIsDisplayed() // TopBar
        composeTestRule.onNodeWithContentDescription("Scanner Area").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Scan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Simulate Scan").assertIsDisplayed()
        composeTestRule.onNodeWithText("No code scanned yet.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan history is empty.").assertIsDisplayed()
    }

    @Test
    fun startScanButtonClick_callsViewModelStartScan_andChangesButtonTextToCancel() {
        composeTestRule.onNodeWithText("Start Scan").performClick()
        verify(mockViewModel).startScan()

        // Simulate ViewModel updating isScanningActive to true
        mockUiState.value = mockUiState.value.copy(isScanningActive = true)
        composeTestRule.onNodeWithText("Cancel Scan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scanning...").assertIsDisplayed() // Check for scanning indicator text
    }

    @Test
    fun cancelScanButtonClick_callsViewModelCancelScan_andChangesButtonTextToStart() {
        // First, set state to scanning
        mockUiState.value = mockUiState.value.copy(isScanningActive = true)
        composeTestRule.onNodeWithText("Cancel Scan").assertIsDisplayed()

        composeTestRule.onNodeWithText("Cancel Scan").performClick()
        verify(mockViewModel).cancelScan()

        // Simulate ViewModel updating isScanningActive to false
        mockUiState.value = mockUiState.value.copy(isScanningActive = false)
        composeTestRule.onNodeWithText("Start Scan").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Scanner Area").assertIsDisplayed() // Camera icon should be back
    }

    @Test
    fun simulateScanButtonClick_callsViewModelProcessScannedCode() {
        composeTestRule.onNodeWithText("Simulate Scan").performClick()
        // Verify with any string as the code is random in UI; actual verification is on VM method
        verify(mockViewModel).processScannedCode(org.mockito.kotlin.any())
    }

    @Test
    fun lastScannedItem_displayedCorrectly_andClearButtonWorks() {
        mockUiState.value = mockUiState.value.copy(lastScannedItem = sampleScan1)

        composeTestRule.onNodeWithText(sampleScan1.content).assertIsDisplayed()
        composeTestRule.onNodeWithText("Scanned: ${sampleScan1.getFormattedTimestamp()}").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear Last Scan").assertIsDisplayed().performClick()
        verify(mockViewModel).clearLastScan()
    }

    @Test
    fun scanHistory_displayedCorrectly_andClearHistoryButtonWorks() {
        mockUiState.value = mockUiState.value.copy(scanHistory = listOf(sampleScan1, sampleScan2))

        composeTestRule.onNodeWithText("Scan History (2)").assertIsDisplayed()
        // Check for items in history (compact view)
        composeTestRule.onNodeWithText(sampleScan1.content, substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleScan2.content, substring = true).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Clear Scan History").assertIsDisplayed().performClick()
        verify(mockViewModel).clearScanHistory()
    }


    @Test
    fun userMessageInUiState_triggersLaunchedEffectAndCallsOnUserMessageShown() {
        mockUiState.value = mockUiState.value.copy(userMessage = "Test Scanner Message")

        composeTestRule.runOnIdle {
            verify(mockViewModel).onUserMessageShown()
        }
    }
}
