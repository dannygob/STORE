package com.example.Store.presentation.purchases

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.Store.presentation.purchases.model.PurchaseItemUi
import com.example.Store.presentation.purchases.ui.PurchasesScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class PurchasesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: PurchasesViewModel
    private val mockUiState = MutableStateFlow(PurchasesUiState())

    private val samplePurchases = listOf(
        PurchaseItemUi(id = "1", productName = "Test Apples", supplierName = "Farm Fresh", quantity = 5, unitPrice = 2.0),
        PurchaseItemUi(id = "2", productName = "Test Milk", supplierName = "Dairy Co", quantity = 2, unitPrice = 1.5)
    )

    @Before
    fun setUp() {
        mockViewModel = mock<PurchasesViewModel> {
            on { uiState } doReturn mockUiState
        }
        // Set initial state for most tests
        mockUiState.value = PurchasesUiState(purchases = samplePurchases, isLoading = false)

        composeTestRule.setContent {
            PurchasesScreen(viewModel = mockViewModel)
        }
    }

    @Test
    fun purchasesScreen_displaysItemsFromViewModel() {
        composeTestRule.onNodeWithText("Test Apples").assertIsDisplayed()
        composeTestRule.onNodeWithText("Qty: 5 @ ${samplePurchases[0].getFormattedUnitPrice()} each").assertIsDisplayed()
        composeTestRule.onNodeWithText("Supplier: Farm Fresh").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total: ${samplePurchases[0].getFormattedTotalPrice()}").assertIsDisplayed()

        composeTestRule.onNodeWithText("Test Milk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Qty: 2 @ ${samplePurchases[1].getFormattedUnitPrice()} each").assertIsDisplayed()
        composeTestRule.onNodeWithText("Supplier: Dairy Co").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total: ${samplePurchases[1].getFormattedTotalPrice()}").assertIsDisplayed()
    }

    @Test
    fun purchasesScreen_showsLoadingIndicatorWhenLoading() {
        mockUiState.value = PurchasesUiState(isLoading = true)
        // Assuming CircularProgressIndicator is shown and items are not
        composeTestRule.onNodeWithText("Test Apples").assertDoesNotExist()
        // Add a more specific check for the loading indicator if it has a testTag
    }

    @Test
    fun purchasesScreen_showsNoPurchasesMessageWhenListIsEmptyAndNotLoading() {
        mockUiState.value = PurchasesUiState(purchases = emptyList(), isLoading = false)
        composeTestRule.onNodeWithText("No purchase history found.").assertIsDisplayed()
    }

    @Test
    fun fabClick_callsViewModelRecordNewPurchasePlaceholder() {
        composeTestRule.onNodeWithContentDescription("Record New Purchase").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Record New Purchase").performClick()
        verify(mockViewModel).recordNewPurchasePlaceholder()
    }

    @Test
    fun purchaseItemClick_callsViewModelViewPurchaseDetailsPlaceholder() {
        val firstPurchase = samplePurchases.first()
        // Click on the Card containing the first purchase's product name
        composeTestRule.onNodeWithText(firstPurchase.productName).performClick()
        verify(mockViewModel).viewPurchaseDetailsPlaceholder(firstPurchase.id)
    }

    @Test
    fun userMessageInUiState_triggersLaunchedEffectAndCallsOnUserMessageShown() {
        mockUiState.value = PurchasesUiState(purchases = samplePurchases, userMessage = "Test Purchase Message")

        composeTestRule.runOnIdle { // Ensure composition and effects have settled
             verify(mockViewModel).onUserMessageShown()
        }
    }
}
