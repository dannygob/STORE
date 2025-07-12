package com.example.Store.presentation.orders

import app.cash.turbine.test
import com.example.Store.presentation.orders.model.OrderStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class OrdersViewModelTest {

    private lateinit var viewModel: OrdersViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OrdersViewModel()
        testDispatcher.scheduler.runCurrent() // Process initial load
    }

    @Test
    fun `initial state loads mock orders and sorts them`() = runTest {
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.isLoading).isFalse()
            assertThat(emission.orders).isNotEmpty()
            assertThat(emission.orders.any { it.customerName == "Alice Wonderland" }).isTrue()
            // Verify sorting (most recent first)
            if (emission.orders.size > 1) {
                for (i in 0 until emission.orders.size - 1) {
                    assertThat(emission.orders[i].orderDate).isAtLeast(emission.orders[i+1].orderDate)
                }
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createNewOrderPlaceholder sets userMessage`() = runTest {
        viewModel.createNewOrderPlaceholder()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Create New Order action triggered (Placeholder).")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `viewOrderDetailsPlaceholder sets userMessage for existing order`() = runTest {
        val firstOrder = viewModel.uiState.value.orders.firstOrNull()
        assertThat(firstOrder).isNotNull()

        firstOrder?.let {
            viewModel.viewOrderDetailsPlaceholder(it.id)
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission.userMessage).isEqualTo("Viewing details for order ${it.orderNumber} (Placeholder).")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `viewOrderDetailsPlaceholder sets userMessage for non-existent order`() = runTest {
        val nonExistentId = "non-existent-order-id"
        viewModel.viewOrderDetailsPlaceholder(nonExistentId)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Order not found.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateOrderStatusPlaceholder updates status and sets userMessage for existing order`() = runTest {
        val orderToUpdate = viewModel.uiState.value.orders.firstOrNull { it.status != OrderStatus.SHIPPED }
        assertThat(orderToUpdate).isNotNull()

        orderToUpdate?.let {
            val newStatus = OrderStatus.SHIPPED
            viewModel.updateOrderStatusPlaceholder(it.id, newStatus)
            viewModel.uiState.test {
                val emission = awaitItem()
                val updatedOrderInState = emission.orders.find { o -> o.id == it.id }
                assertThat(updatedOrderInState?.status).isEqualTo(newStatus)
                assertThat(emission.userMessage).isEqualTo("Order ${updatedOrderInState?.orderNumber} status updated to ${newStatus.getDisplayValue()} (Placeholder).")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `updateOrderStatusPlaceholder sets userMessage for non-existent order`() = runTest {
        val nonExistentId = "non-existent-order-id-for-status-update"
        val newStatus = OrderStatus.DELIVERED
        viewModel.updateOrderStatusPlaceholder(nonExistentId, newStatus)
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("Failed to update status for order ID $nonExistentId (not found).")
            // Also ensure no order status actually changed
            val orderWithNewStatus = emission.orders.find { it.status == newStatus && it.id == nonExistentId}
            assertThat(orderWithNewStatus).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onUserMessageShown clears userMessage`() = runTest {
        viewModel.createNewOrderPlaceholder()
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
