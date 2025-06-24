package com.example.store.presentation.sales

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.store.presentation.sales.model.SaleItemUi
import com.example.store.presentation.sales.model.SoldItemSimple
import com.example.store.presentation.sales.ui.SalesScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify

class SalesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: SalesViewModel
    private val mockUiState = MutableStateFlow(SalesUiState())

    private val sampleSales = listOf(
        SaleItemUi(
            id = "1",
            transactionId = "SALE-001",
            itemsSoldSummary = "Apples (1), Bananas (2)",
            items = listOf(SoldItemSimple("Apples", 1, 0.5), SoldItemSimple("Bananas", 2, 0.3)),
            totalAmount = 1.1,
            customerName = "Customer A"
        ),
        SaleItemUi(
            id = "2",
            transactionId = "SALE-002",
            itemsSoldSummary = "Milk (1)",
            items = listOf(SoldItemSimple("Milk", 1, 1.2)),
            totalAmount = 1.2
        )
    )

    @Before
    fun setUp() {
        mockViewModel = mock<SalesViewModel> {
            on { uiState } doReturn mockUiState
        }
        mockUiState.value = SalesUiState(sales = sampleSales, isLoading = false)

        composeTestRule.setContent {
            SalesScreen(viewModel = mockViewModel)
        }
    }

    @Test
    fun salesScreen_displaysItemsFromViewModel() {
        // Test first sale item
        composeTestRule.onNodeWithText("ID: SALE-001").assertIsDisplayed()
        composeTestRule.onNodeWithText("Items: Apples (1), Bananas (2)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Customer: Customer A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total: ${sampleSales[0].getFormattedTotalAmount()}").assertIsDisplayed()

        // Test second sale item
        composeTestRule.onNodeWithText("ID: SALE-002").assertIsDisplayed()
        composeTestRule.onNodeWithText("Items: Milk (1)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total: ${sampleSales[1].getFormattedTotalAmount()}").assertIsDisplayed()
        // Check that "Customer:" is not displayed if customerName is null
        composeTestRule.onNodeWithText("Customer: ", substring = true).assertDoesNotExist()
    }

    @Test
    fun salesScreen_showsLoadingIndicatorWhenLoading() {
        mockUiState.value = SalesUiState(isLoading = true)
        composeTestRule.onNodeWithText("ID: SALE-001").assertDoesNotExist()
        // Add a more specific check for the loading indicator if it has a testTag
    }

    @Test
    fun salesScreen_showsNoSalesMessageWhenListIsEmptyAndNotLoading() {
        mockUiState.value = SalesUiState(sales = emptyList(), isLoading = false)
        composeTestRule.onNodeWithText("No sales transactions found.").assertIsDisplayed()
    }

    @Test
    fun fabClick_callsViewModelRecordNewSalePlaceholder() {
        composeTestRule.onNodeWithContentDescription("Record New Sale").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Record New Sale").performClick()
        verify(mockViewModel).recordNewSalePlaceholder()
    }

    @Test
    fun saleItemClick_callsViewModelViewSaleDetailsPlaceholder() {
        val firstSale = sampleSales.first()
        // Click on the Card containing the first sale's transaction ID
        composeTestRule.onNodeWithText("ID: ${firstSale.transactionId}").performClick()
        verify(mockViewModel).viewSaleDetailsPlaceholder(firstSale.id)
    }

    @Test
    fun userMessageInUiState_triggersLaunchedEffectAndCallsOnUserMessageShown() {
        mockUiState.value = SalesUiState(sales = sampleSales, userMessage = "Test Sales Message")

        composeTestRule.runOnIdle { // Ensure composition and effects have settled
            verify(mockViewModel).onUserMessageShown()
        }
    }
}
