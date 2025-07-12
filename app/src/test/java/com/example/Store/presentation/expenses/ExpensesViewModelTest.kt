package com.example.Store.presentation.expenses

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ExpensesViewModelTest {

    private lateinit var viewModel: ExpensesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ExpensesViewModel()
        testDispatcher.scheduler.runCurrent() // Process initial load
    }

    @Test
    fun `initial state loads mock expenses, calculates total, and sorts them`() = runTest {
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.isLoading).isFalse()
            assertThat(emission.expenses).isNotEmpty()
            assertThat(emission.expenses.any { it.category == "Rent" }).isTrue()

            val expectedTotal = emission.expenses.sumOf { it.amount }
            assertThat(emission.totalExpenses).isEqualTo(expectedTotal)

            // Verify sorting (most recent first)
            if (emission.expenses.size > 1) {
                for (i in 0 until emission.expenses.size - 1) {
                    assertThat(emission.expenses[i].expenseDate).isAtLeast(emission.expenses[i+1].expenseDate)
                }
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addExpensePlaceholder sets userMessage`() = runTest {
        viewModel.addExpensePlaceholder()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Add New Expense action triggered (Placeholder).")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `viewExpenseDetailsPlaceholder sets userMessage for existing expense`() = runTest {
        val firstExpense = viewModel.uiState.value.expenses.firstOrNull()
        assertThat(firstExpense).isNotNull()

        firstExpense?.let {
            viewModel.viewExpenseDetailsPlaceholder(it.id)
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission.userMessage).isEqualTo("Viewing details for expense: '${it.description}' (Placeholder).")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `viewExpenseDetailsPlaceholder sets userMessage for non-existent expense`() = runTest {
        val nonExistentId = "non-existent-expense-id"
        viewModel.viewExpenseDetailsPlaceholder(nonExistentId)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Expense not found.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addActualExpense adds item, updates total, sorts, and sets userMessage`() = runTest {
        val initialExpenses = viewModel.uiState.value.expenses
        val initialTotal = viewModel.uiState.value.totalExpenses

        val newDesc = "New Test Expense"
        val newCat = "Test Category"
        val newAmount = 100.0

        viewModel.addActualExpense(newDesc, newCat, newAmount)
        testDispatcher.scheduler.runCurrent() // Ensure updates are processed

        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.expenses.size).isEqualTo(initialExpenses.size + 1)
            val addedExpense = emission.expenses.find { it.description == newDesc }
            assertThat(addedExpense).isNotNull()
            assertThat(addedExpense?.category).isEqualTo(newCat)
            assertThat(addedExpense?.amount).isEqualTo(newAmount)

            assertThat(emission.totalExpenses).isEqualTo(initialTotal + newAmount)
            assertThat(emission.userMessage).isEqualTo("'$newDesc' added to expenses.")

            // Verify sorting includes the new item correctly (most recent first)
            if (emission.expenses.size > 1) {
                for (i in 0 until emission.expenses.size - 1) {
                    assertThat(emission.expenses[i].expenseDate).isAtLeast(emission.expenses[i+1].expenseDate)
                }
            }
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onUserMessageShown clears userMessage`() = runTest {
        viewModel.addExpensePlaceholder()
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
