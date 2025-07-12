package com.example.Store.presentation.sales

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SalesViewModelTest {

    private lateinit var viewModel: SalesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SalesViewModel()
        // Advance past the init block's loadSalesHistory coroutine
        testDispatcher.scheduler.runCurrent()
    }

    @Test
    fun `initial state loads mock sales and sorts them`() = runTest {
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.isLoading).isFalse()
            assertThat(emission.sales).isNotEmpty()
            // Check for a known item from mock data
            assertThat(emission.sales.any { it.itemsSoldSummary.contains("Apples") }).isTrue()
            // Verify sorting (most recent first)
            if (emission.sales.size > 1) {
                for (i in 0 until emission.sales.size - 1) {
                    assertThat(emission.sales[i].saleDate).isAtLeast(emission.sales[i+1].saleDate)
                }
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recordNewSalePlaceholder sets userMessage`() = runTest {
        viewModel.recordNewSalePlaceholder()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Record New Sale action triggered (Placeholder).")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `viewSaleDetailsPlaceholder sets userMessage for existing sale`() = runTest {
        val firstSale = viewModel.uiState.value.sales.firstOrNull()
        assertThat(firstSale).isNotNull()

        firstSale?.let {
            viewModel.viewSaleDetailsPlaceholder(it.id)
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission.userMessage).isEqualTo("Viewing details for sale ${it.transactionId} (Placeholder).")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `viewSaleDetailsPlaceholder sets userMessage for non-existent sale`() = runTest {
        val nonExistentId = "non-existent-sale-id"
        viewModel.viewSaleDetailsPlaceholder(nonExistentId)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Sale not found.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUserMessageShown clears userMessage`() = runTest {
        // Set a message first
        viewModel.recordNewSalePlaceholder()
        testDispatcher.scheduler.runCurrent() // Ensure state update

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
