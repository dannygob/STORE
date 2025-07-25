package com.example.store.presentation.orders

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.store.presentation.orders.model.OrderItemUi
import com.example.store.presentation.orders.model.OrderStatus
import com.example.store.presentation.orders.ui.OrdersScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class OrdersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: OrdersViewModel
    private val mockUiState = MutableStateFlow(OrdersUiState())

    private val sampleOrders = listOf(
        OrderItemUi(
            id = "1",
            orderNumber = "ORD-001",
            customerName = "Customer Alpha",
            status = OrderStatus.PENDING,
            totalAmount = 100.0,
            itemSummary = "Item A, Item B"
        ),
        OrderItemUi(
            id = "2",
            orderNumber = "ORD-002",
            customerName = "Customer Beta",
            status = OrderStatus.SHIPPED,
            totalAmount = 50.0,
            itemSummary = "Item C"
        )
    )

    @Before
    fun setUp() {
        mockViewModel = mock<OrdersViewModel> {
            on { uiState } doReturn mockUiState
        }
        mockUiState.value = OrdersUiState(orders = sampleOrders, isLoading = false)

        composeTestRule.setContent {
            OrdersScreen(viewModel = mockViewModel)
        }
    }

    @Test
    fun ordersScreen_displaysItemsFromViewModel() {
        // Test first order
        composeTestRule.onNodeWithText("ORD-001").assertIsDisplayed()
        composeTestRule.onNodeWithText("Customer: Customer Alpha").assertIsDisplayed()
        composeTestRule.onNodeWithText(OrderStatus.PENDING.getDisplayValue()).assertIsDisplayed()
        composeTestRule.onNodeWithText("Total: ${sampleOrders[0].getFormattedTotalAmount()}").assertIsDisplayed()

        // Test second order
        composeTestRule.onNodeWithText("ORD-002").assertIsDisplayed()
        composeTestRule.onNodeWithText("Customer: Customer Beta").assertIsDisplayed()
        composeTestRule.onNodeWithText(OrderStatus.SHIPPED.getDisplayValue()).assertIsDisplayed()
        composeTestRule.onNodeWithText("Total: ${sampleOrders[1].getFormattedTotalAmount()}").assertIsDisplayed()
    }

    @Test
    fun ordersScreen_showsLoadingIndicatorWhenLoading() {
        mockUiState.value = OrdersUiState(isLoading = true)
        composeTestRule.onNodeWithText("ORD-001").assertDoesNotExist()
        // Add a more specific check for the loading indicator if it has a testTag
    }

    @Test
    fun ordersScreen_showsNoOrdersMessageWhenListIsEmptyAndNotLoading() {
        mockUiState.value = OrdersUiState(orders = emptyList(), isLoading = false)
        composeTestRule.onNodeWithText("No orders found.").assertIsDisplayed()
    }

    @Test
    fun fabClick_callsViewModelCreateNewOrderPlaceholder() {
        composeTestRule.onNodeWithContentDescription("Create New Order").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Create New Order").performClick()
        verify(mockViewModel).createNewOrderPlaceholder()
    }

    @Test
    fun orderItemClick_callsViewModelViewOrderDetailsPlaceholder() {
        val firstOrder = sampleOrders.first()
        composeTestRule.onNodeWithText(firstOrder.orderNumber).performClick() // Click the card via order number text
        verify(mockViewModel).viewOrderDetailsPlaceholder(firstOrder.id)
    }

    @Test
    fun statusMenuClick_opensMenuAndItemClickCallsViewModelUpdateStatus() {
        val firstOrder = sampleOrders.first()
        val newStatus = OrderStatus.PROCESSING

        // Click the "MoreVert" icon for the first order
        composeTestRule.onNodeWithContentDescription("Update status for ${firstOrder.orderNumber}")
            .assertIsDisplayed()
            .performClick()

        // Menu should be open, click on the new status
        composeTestRule.onNodeWithText(newStatus.getDisplayValue())
            .assertIsDisplayed()
            .performClick()

        // Verify ViewModel was called
        verify(mockViewModel).updateOrderStatusPlaceholder(firstOrder.id, newStatus)
    }

    @Test
    fun userMessageInUiState_triggersLaunchedEffectAndCallsOnUserMessageShown() {
        mockUiState.value = OrdersUiState(orders = sampleOrders, userMessage = "Test Order Message")

        composeTestRule.runOnIdle {
            verify(mockViewModel).onUserMessageShown()
        }
    }
}
