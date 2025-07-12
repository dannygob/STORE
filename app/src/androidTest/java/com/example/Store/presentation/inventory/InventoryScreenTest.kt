package com.example.Store.presentation.inventory

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.Store.presentation.inventory.model.InventoryItemUi
import com.example.Store.presentation.inventory.ui.InventoryScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// Using createComposeRule() for more direct Composable testing without full Activity context
// For tests involving NavController that's part of a larger graph, createAndroidComposeRule might be needed.
class InventoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock ViewModel
    private lateinit var mockViewModel: InventoryViewModel
    private val mockUiState = MutableStateFlow(InventoryUiState())

    private val sampleItems = listOf(
        InventoryItemUi(id = "1", name = "Test Apple", quantity = 10, price = 1.0),
        InventoryItemUi(id = "2", name = "Test Banana", quantity = 5, price = 0.5)
    )

    @Before
    fun setUp() {
        // Mock the ViewModel and its StateFlow
        mockViewModel = mock<InventoryViewModel> {
            on { uiState } doReturn mockUiState
        }
        // Set initial state for most tests
        mockUiState.value = InventoryUiState(items = sampleItems, isLoading = false)

        composeTestRule.setContent {
            // It's good practice to wrap in a MaterialTheme if your composables use it,
            // but for simple tests focusing on InventoryScreen, it might not be strictly necessary
            // if it doesn't rely on theme attributes directly for basic layout/text.
            // For robustness: MaterialTheme { InventoryScreen(viewModel = mockViewModel) }
            InventoryScreen(viewModel = mockViewModel)
        }
    }

    @Test
    fun inventoryScreen_displaysItemsFromViewModel() {
        composeTestRule.onNodeWithText("Test Apple").assertIsDisplayed()
        composeTestRule.onNodeWithText("Qty: 10").assertIsDisplayed()
        composeTestRule.onNodeWithText("Price: $1.00").assertIsDisplayed()

        composeTestRule.onNodeWithText("Test Banana").assertIsDisplayed()
        composeTestRule.onNodeWithText("Qty: 5").assertIsDisplayed()
        composeTestRule.onNodeWithText("Price: $0.50").assertIsDisplayed()
    }

    @Test
    fun inventoryScreen_showsLoadingIndicatorWhenLoading() {
        mockUiState.value = InventoryUiState(isLoading = true)
        composeTestRule.onNode(isRoot()).printToLog("LoadingState") // For debugging
        // Check for a CircularProgressIndicator. A common way is to check for its test tag if you've set one.
        // Or, if it's the only prominent element, you might check other elements are not displayed.
        // For simplicity, we'll assume it's the only thing and other text isn't there.
        // This is a weak assertion, better to use testTag on CircularProgressIndicator.
        composeTestRule.onNodeWithText("Test Apple").assertDoesNotExist() // Assuming items are not shown when loading
        // Add specific check for progress indicator if possible, e.g., by testTag
    }

    @Test
    fun inventoryScreen_showsNoItemsMessageWhenListIsEmptyAndNotLoading() {
        mockUiState.value = InventoryUiState(items = emptyList(), isLoading = false)
        composeTestRule.onNodeWithText("No items in inventory.").assertIsDisplayed()
    }

    @Test
    fun fabClick_showsToastPlaceholder() {
        // The actual Toast is tricky to test with composeTestRule directly as it's outside the composition.
        // We will verify the ViewModel interaction that *would* lead to a Toast.
        // In the actual InventoryScreen, FAB click shows a Toast directly for now.
        // So, this test is more about the FAB being clickable.
        // To test the Toast text itself, you'd need Espresso with onToast.
        // For now, let's just ensure the FAB is there.
        composeTestRule.onNodeWithContentDescription("Add Item").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Item").performClick()
        // Verification of Toast message would ideally be done via Espresso or a test rule that captures Toasts.
        // Since the FAB in the current implementation directly shows a toast,
        // we can't easily verify the text with Compose UI testing alone.
        // If the FAB called a ViewModel method, we could verify that:
        // Mockito.verify(mockViewModel).someMethodRelatedToFab()
    }

    @Test
    fun deleteButtonClick_callsViewModelDelete() {
        val itemToDelete = sampleItems.first()
        composeTestRule.onNodeWithContentDescription("Delete ${itemToDelete.name}").performClick()
        Mockito.verify(mockViewModel).deleteItem(itemToDelete.id)
    }

    @Test
    fun editButtonClick_callsViewModelEditPlaceholder() {
        val itemToEdit = sampleItems.first()
        composeTestRule.onNodeWithContentDescription("Edit ${itemToEdit.name}").performClick()
        Mockito.verify(mockViewModel).editItemPlaceholder(itemToEdit.id)
    }

    @Test
    fun userMessageInUiState_triggersLaunchedEffectAndCallsOnUserMessageShown() {
        // Set a user message
        mockUiState.value = InventoryUiState(items = sampleItems, userMessage = "Test Message")

        // Recompose if necessary, though collectAsState should handle it.
        // composeTestRule.waitForIdle() // Ensure LaunchedEffect has a chance to run

        // Verify that onUserMessageShown was called (due to LaunchedEffect)
        // This needs careful handling of how LaunchedEffect and StateFlow emissions are tested.
        // The LaunchedEffect will trigger the Toast and then call onUserMessageShown.
        // We need to ensure the recomposition happens and the effect runs.

        // Due to the nature of LaunchedEffect and state consumption,
        // directly verifying onUserMessageShown after setting the state can be tricky
        // without more advanced test schedulers or waiting mechanisms for effects.
        // The LaunchedEffect should consume the message.
        // A simple way to check if the consumption logic is wired:
        // After the message is set, if the ViewModel's onUserMessageShown is called,
        // the message should eventually be nullified in a subsequent state if the VM updates it.

        // For this test, we'll verify the call. It assumes the LaunchedEffect runs.
        // This might require advancing clocks or specific dispatcher configurations in more complex scenarios.
        composeTestRule.runOnIdle { // Ensure composition and effects have settled
             Mockito.verify(mockViewModel).onUserMessageShown()
        }
    }
}
