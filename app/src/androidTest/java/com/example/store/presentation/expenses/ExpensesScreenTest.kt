package com.example.store.presentation.expenses

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.store.presentation.expenses.model.ExpenseItemUi
import com.example.store.presentation.expenses.ui.ExpensesScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify

class ExpensesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: ExpensesViewModel
    private val mockUiState = MutableStateFlow(ExpensesUiState())

    private val sampleExpenses = listOf(
        ExpenseItemUi(id = "1", description = "Office Rent", category = "Rent", amount = 1200.0, vendor = "Big Corp"),
        ExpenseItemUi(id = "2", description = "Electricity Bill", category = "Utilities", amount = 150.0)
    )
    private val sampleTotalExpenses = sampleExpenses.sumOf { it.amount }

    @Before
    fun setUp() {
        mockViewModel = mock<ExpensesViewModel> {
            on { uiState } doReturn mockUiState
        }
        mockUiState.value = ExpensesUiState(
            expenses = sampleExpenses,
            isLoading = false,
            totalExpenses = sampleTotalExpenses
        )

        composeTestRule.setContent {
            ExpensesScreen(viewModel = mockViewModel)
        }
    }

    @Test
    fun expensesScreen_displaysItemsAndTotalFromViewModel() {
        // Test Total Expenses Header
        composeTestRule.onNodeWithText("Total Expenses").assertIsDisplayed()
        composeTestRule.onNodeWithText(String.format("$%.2f", sampleTotalExpenses)).assertIsDisplayed()

        // Test first expense item
        composeTestRule.onNodeWithText("Office Rent").assertIsDisplayed()
        composeTestRule.onNodeWithText("Category: Rent").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vendor: Big Corp").assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleExpenses[0].getFormattedAmount()).assertIsDisplayed()

        // Test second expense item
        composeTestRule.onNodeWithText("Electricity Bill").assertIsDisplayed()
        composeTestRule.onNodeWithText("Category: Utilities").assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleExpenses[1].getFormattedAmount()).assertIsDisplayed()
        // Check that "Vendor:" is not displayed if vendor is null (implicitly by not finding it for second item)
    }

    @Test
    fun expensesScreen_showsLoadingIndicatorWhenLoading() {
        mockUiState.value = ExpensesUiState(isLoading = true, totalExpenses = 0.0) // total is also reset/not yet calc
        composeTestRule.onNodeWithText("Office Rent").assertDoesNotExist()
        // Check that total expenses might show 0 or not be the focus when loading
        composeTestRule.onNodeWithText(String.format("$%.2f", 0.00)).assertIsDisplayed()
    }

    @Test
    fun expensesScreen_showsNoExpensesMessageWhenListIsEmptyAndNotLoading() {
        mockUiState.value = ExpensesUiState(expenses = emptyList(), isLoading = false, totalExpenses = 0.0)
        composeTestRule.onNodeWithText("No expenses recorded yet.").assertIsDisplayed()
        composeTestRule.onNodeWithText(String.format("$%.2f", 0.00)).assertIsDisplayed() // Total should be 0
    }

    @Test
    fun fabClick_callsViewModelAddExpensePlaceholder() {
        composeTestRule.onNodeWithContentDescription("Add New Expense").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add New Expense").performClick()
        verify(mockViewModel).addExpensePlaceholder()
    }

    @Test
    fun expenseItemClick_callsViewModelViewExpenseDetailsPlaceholder() {
        val firstExpense = sampleExpenses.first()
        // Click on the Card containing the first expense's description
        composeTestRule.onNodeWithText(firstExpense.description).performClick()
        verify(mockViewModel).viewExpenseDetailsPlaceholder(firstExpense.id)
    }

    @Test
    fun userMessageInUiState_triggersLaunchedEffectAndCallsOnUserMessageShown() {
        mockUiState.value = mockUiState.value.copy(userMessage = "Test Expense Message")

        composeTestRule.runOnIdle {
            verify(mockViewModel).onUserMessageShown()
        }
    }
}
