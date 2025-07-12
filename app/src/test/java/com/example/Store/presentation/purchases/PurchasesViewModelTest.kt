package com.example.Store.presentation.purchases

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PurchasesViewModelTest {

    private lateinit var viewModel: PurchasesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PurchasesViewModel()
        // Advance past the init block's loadPurchaseHistory coroutine
        testDispatcher.scheduler.runCurrent()
    }

    @Test
    fun `initial state loads mock purchases`() = runTest {
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.isLoading).isFalse() // Should be false after init load
            assertThat(emission.purchases).isNotEmpty()
            // Check for a known item from mock data
            assertThat(emission.purchases.any { it.productName == "Apples (Box)" }).isTrue()
            // Check if sorted by date descending (most recent first)
            if (emission.purchases.size > 1) {
                assertThat(emission.purchases[0].purchaseDate)
                    .isAtLeast(emission.purchases[1].purchaseDate)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recordNewPurchasePlaceholder sets userMessage`() = runTest {
        viewModel.recordNewPurchasePlaceholder()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Record New Purchase action triggered (Placeholder).")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `viewPurchaseDetailsPlaceholder sets userMessage for existing item`() = runTest {
        val firstPurchase = viewModel.uiState.value.purchases.firstOrNull()
        assertThat(firstPurchase).isNotNull()

        firstPurchase?.let {
            viewModel.viewPurchaseDetailsPlaceholder(it.id)
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission.userMessage).isEqualTo("Viewing details for purchase of '${it.productName}' (Placeholder).")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `viewPurchaseDetailsPlaceholder sets userMessage for non-existent item`() = runTest {
        val nonExistentId = "non-existent-id-123"
        viewModel.viewPurchaseDetailsPlaceholder(nonExistentId)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Purchase not found.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUserMessageShown clears userMessage`() = runTest {
        // Set a message first
        viewModel.recordNewPurchasePlaceholder()
        testDispatcher.scheduler.runCurrent() // Ensure state update is processed

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
