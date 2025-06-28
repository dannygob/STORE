package com.example.store.presentation.scanner

import app.cash.turbine.test
import com.example.store.presentation.scanner.model.ScannedDataUi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ScannerViewModelTest {

    private lateinit var viewModel: ScannerViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScannerViewModel()
        // No initial loading, so no need to advance dispatcher here for init
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.lastScannedItem).isNull()
            assertThat(emission.scanHistory).isEmpty()
            assertThat(emission.isScanningActive).isFalse()
            assertThat(emission.userMessage).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startScan updates state and userMessage`() = runTest {
        viewModel.startScan()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.isScanningActive).isTrue()
            assertThat(emission.userMessage).isEqualTo("Scan started... (Point camera at a code - Placeholder)")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `processScannedCode updates lastScannedItem, history, and userMessage`() = runTest {
        val testCode = "TEST12345"
        viewModel.processScannedCode(testCode)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.lastScannedItem).isNotNull()
            assertThat(emission.lastScannedItem?.content).isEqualTo(testCode)
            assertThat(emission.scanHistory).hasSize(1)
            assertThat(emission.scanHistory.first().content).isEqualTo(testCode)
            assertThat(emission.isScanningActive).isFalse() // Should turn off after processing
            assertThat(emission.userMessage).isEqualTo("Scanned: $testCode")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `processScannedCode respects max history size`() = runTest {
        val maxHistory = 10 // As defined in ViewModel
        for (i in 1..(maxHistory + 5)) {
            viewModel.processScannedCode("CODE-$i")
            testDispatcher.scheduler.runCurrent() // ensure updates are processed for each item
        }

        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.scanHistory).hasSize(maxHistory)
            assertThat(emission.scanHistory.first().content).isEqualTo("CODE-6") // Oldest item should be CODE-6
            assertThat(emission.scanHistory.last().content).isEqualTo("CODE-15") // Newest item
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cancelScan updates state and userMessage if scanning was active`() = runTest {
        viewModel.startScan() // Make scanning active
        testDispatcher.scheduler.runCurrent()

        viewModel.cancelScan()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.isScanningActive).isFalse()
            assertThat(emission.userMessage).isEqualTo("Scan cancelled.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cancelScan does nothing if scanning was not active`() = runTest {
        // Initial state is not scanning
        val initialState = viewModel.uiState.value
        viewModel.cancelScan()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(initialState) // State should not change
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `clearLastScan clears item and sets userMessage if item exists`() = runTest {
        viewModel.processScannedCode("ANYCODE")
        testDispatcher.scheduler.runCurrent()
        assertThat(viewModel.uiState.value.lastScannedItem).isNotNull()

        viewModel.clearLastScan()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.lastScannedItem).isNull()
            assertThat(emission.userMessage).isEqualTo("Last scan cleared.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearLastScan sets message if no item to clear`() = runTest {
        assertThat(viewModel.uiState.value.lastScannedItem).isNull()
        viewModel.clearLastScan()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Nothing to clear.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearScanHistory clears history and last scan, sets userMessage`() = runTest {
        viewModel.processScannedCode("CODE1")
        testDispatcher.scheduler.runCurrent()
        viewModel.processScannedCode("CODE2")
        testDispatcher.scheduler.runCurrent()

        assertThat(viewModel.uiState.value.scanHistory).isNotEmpty()
        assertThat(viewModel.uiState.value.lastScannedItem).isNotNull()

        viewModel.clearScanHistory()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.scanHistory).isEmpty()
            assertThat(emission.lastScannedItem).isNull()
            assertThat(emission.userMessage).isEqualTo("Scan history cleared.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearScanHistory sets message if history already empty`() = runTest {
        assertThat(viewModel.uiState.value.scanHistory).isEmpty()
        viewModel.clearScanHistory()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Scan history is already empty.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUserMessageShown clears userMessage`() = runTest {
        viewModel.startScan() // This sets a user message
        testDispatcher.scheduler.runCurrent()
        assertThat(viewModel.uiState.value.userMessage).isNotNull()

        viewModel.onUserMessageShown()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
