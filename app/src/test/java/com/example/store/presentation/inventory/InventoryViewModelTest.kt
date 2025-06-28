package com.example.store.presentation.inventory

import app.cash.turbine.test
import com.example.store.presentation.inventory.model.InventoryItemUi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class InventoryViewModelTest {

    private lateinit var viewModel: InventoryViewModel
    private val testDispatcher = StandardTestDispatcher() // For more control if needed, UnconfinedTestDispatcher also works for many cases

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = InventoryViewModel()
        // Advance past the init block's loadInventoryItems coroutine
        // For StandardTestDispatcher, you need to advance time or runCurrent.
        // If using UnconfinedTestDispatcher, it might run eagerly.
        // runCurrent() will execute tasks scheduled at the current virtual time.
        testDispatcher.scheduler.runCurrent()
    }

    @Test
    fun `initial state loads mock items`() = runTest {
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.isLoading).isFalse() // Should be false after init load
            assertThat(emission.items).isNotEmpty()
            assertThat(emission.items.any { it.name == "Apples" }).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addItem adds new item and sets userMessage`() = runTest {
        val initialItemCount = viewModel.uiState.value.items.size
        val itemName = "Test Item"
        val itemQuantity = 10
        val itemPrice = 5.99

        viewModel.addItem(itemName, itemQuantity, itemPrice)

        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.items.size).isEqualTo(initialItemCount + 1)
            val addedItem = emission.items.find { it.name == itemName }
            assertThat(addedItem).isNotNull()
            assertThat(addedItem?.quantity).isEqualTo(itemQuantity)
            assertThat(addedItem?.price).isEqualTo(itemPrice)
            assertThat(emission.userMessage).isEqualTo("Item '$itemName' added.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteItem removes item and sets userMessage`() = runTest {
        // Get an item to delete (e.g., the first mock item)
        val itemToDelete = viewModel.uiState.value.items.firstOrNull()
        assertThat(itemToDelete).isNotNull()
        itemToDelete?.let {
            val initialItemCount = viewModel.uiState.value.items.size
            viewModel.deleteItem(it.id)

            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission.items.size).isEqualTo(initialItemCount - 1)
                assertThat(emission.items.find { item -> item.id == it.id }).isNull()
                assertThat(emission.userMessage).isEqualTo("Item '${it.name}' deleted.")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `deleteItem with non-existent id sets appropriate userMessage`() = runTest {
        val nonExistentId = "non-existent-id"
        val initialItemCount = viewModel.uiState.value.items.size
        viewModel.deleteItem(nonExistentId)

        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.items.size).isEqualTo(initialItemCount) // No change in item count
            assertThat(emission.userMessage).isEqualTo("Item not found.")
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `editItemPlaceholder sets userMessage`() = runTest {
        val itemToEdit = viewModel.uiState.value.items.firstOrNull()
        assertThat(itemToEdit).isNotNull()
        itemToEdit?.let {
            viewModel.editItemPlaceholder(it.id)
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission.userMessage).isEqualTo("Edit action for '${it.name}' triggered.")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `editItemPlaceholder with non-existent id sets appropriate userMessage`() = runTest {
        val nonExistentId = "non-existent-id"
        viewModel.editItemPlaceholder(nonExistentId)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Item not found for edit.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUserMessageShown clears userMessage`() = runTest {
        // First, set a message
        viewModel.addItem("Temp Item", 1, 1.0)
        // Advance past the addItem coroutine if it's not immediate
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
