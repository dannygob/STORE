package com.example.Store.presentation.dashboard

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.Store.presentation.dashboard.model.NotificationItemUi
import com.example.Store.presentation.dashboard.model.NotificationType
import com.example.Store.presentation.dashboard.ui.DashboardScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DashboardNotificationFeatureTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: DashboardViewModel
    private val mockUiStateFlow = MutableStateFlow(DashboardUiState())

    private val sampleNotification1 = NotificationItemUi(id = "n1", title = "Order Update", message = "Order #123 shipped", type = NotificationType.ORDER_DELIVERED)
    private val sampleNotification2 = NotificationItemUi(id = "n2", title = "Low Stock", message = "Apples running low", type = NotificationType.LOW_STOCK, isRead = true)
    private val sampleNotifications = listOf(sampleNotification1, sampleNotification2)

    @Before
    fun setUp() {
        mockViewModel = mock<DashboardViewModel> {
            on { uiState } doReturn mockUiStateFlow
        }
        // Set initial state for most tests
        mockUiStateFlow.value = DashboardUiState(
            notifications = sampleNotifications,
            unreadNotificationCount = sampleNotifications.count { !it.isRead }
        )

        composeTestRule.setContent {
            // DashboardScreen requires a NavController, provide a dummy one for isolated testing
            val navController = rememberNavController()
            DashboardScreen(navController = navController, viewModel = mockViewModel)
        }
    }

    @Test
    fun notificationBell_isDisplayed_withBadgeWhenUnread() {
        composeTestRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
        // Verify badge text (unread count is 1 from sampleNotifications)
        composeTestRule.onNodeWithText("1").assertIsDisplayed() // Badge text
    }

    @Test
    fun notificationBell_isDisplayed_withoutBadgeWhenAllRead() {
        mockUiStateFlow.value = DashboardUiState(
            notifications = sampleNotifications.map { it.copy(isRead = true) },
            unreadNotificationCount = 0
        )
        composeTestRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithText("0").assertDoesNotExist() // Badge text should not exist
        composeTestRule.onNodeWithText("1").assertDoesNotExist()
    }

    @Test
    fun clickingNotificationBell_opensAndClosesPanel() {
        // Panel should not be visible initially by default (DropdownMenu behavior)
        composeTestRule.onNodeWithText("Order Update").assertDoesNotExist() // Check for an item in the panel

        // Click to open
        composeTestRule.onNodeWithContentDescription("Notifications").performClick()
        composeTestRule.onNodeWithText("Order Update").assertIsDisplayed() // Item from panel is now visible

        // Click again (or on dismiss area, but bell is easier to target)
        // Note: Clicking bell again might not close DropdownMenu if it's not anchored to it.
        // A more robust way is to press back or click outside if possible.
        // For simplicity, if clicking the bell is meant to toggle, this is fine.
        // However, DropdownMenu's onDismissRequest handles clicks outside.
        // Let's test dismissal by attempting to click an item which should close it.
        composeTestRule.onNodeWithText("Order Update").performClick() // This should close the panel
        composeTestRule.onNodeWithText("Order Update").assertDoesNotExist() // Panel should be closed
    }

    @Test
    fun notificationsPanel_displaysItemsCorrectly() {
        composeTestRule.onNodeWithContentDescription("Notifications").performClick() // Open panel

        // Check for notification 1 details
        composeTestRule.onNodeWithText(sampleNotification1.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleNotification1.message).assertIsDisplayed()

        // Check for notification 2 details
        composeTestRule.onNodeWithText(sampleNotification2.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleNotification2.message).assertIsDisplayed()
    }

    @Test
    fun clickingNotificationItem_callsMarkAsRead_andClosesPanel() {
        composeTestRule.onNodeWithContentDescription("Notifications").performClick() // Open panel
        composeTestRule.onNodeWithText(sampleNotification1.title).performClick()

        verify(mockViewModel).markAsRead(sampleNotification1.id)
        // Panel should close after click, so item title should no longer be found as a top-level node.
        // This assumes the DropdownMenu is dismissed.
        composeTestRule.onNodeWithText(sampleNotification1.title).assertDoesNotExist()
    }

    @Test
    fun clickingDismissOnNotificationItem_callsDismissNotification() {
        composeTestRule.onNodeWithContentDescription("Notifications").performClick() // Open panel

        // Find dismiss button for the first notification
        // This assumes the content description is unique enough or we target a specific instance
        composeTestRule.onAllNodesWithContentDescription("Dismiss notification").assertCountEquals(sampleNotifications.size)
        composeTestRule.onAllNodesWithContentDescription("Dismiss notification")[0].performClick()


        verify(mockViewModel).dismissNotification(sampleNotification1.id)
    }

    @Test
    fun clickingMarkAllAsRead_callsViewModelMarkAllAsRead() {
        // Ensure there's an unread notification to make the button appear
        mockUiStateFlow.value = DashboardUiState(
            notifications = sampleNotifications, // sampleNotification1 is unread
            unreadNotificationCount = 1
        )
        composeTestRule.onNodeWithContentDescription("Notifications").performClick() // Open panel
        composeTestRule.onNodeWithText("Mark all as read").performClick()
        verify(mockViewModel).markAllAsRead()
    }

    @Test
    fun clickingDismissAll_callsViewModelDismissAllNotifications() {
        composeTestRule.onNodeWithContentDescription("Notifications").performClick() // Open panel
        composeTestRule.onNodeWithText("Dismiss all").performClick()
        verify(mockViewModel).dismissAllNotifications()
    }

    @Test
    fun userMessageInUiState_triggersLaunchedEffectAndCallsOnUserMessageShown() {
        // This test is more about the DashboardScreen's general message handling
        mockUiStateFlow.value = mockUiStateFlow.value.copy(userMessage = "Dashboard Test Message")

        composeTestRule.runOnIdle { // Ensure composition and effects have settled
            verify(mockViewModel).onUserMessageShown()
        }
    }
}
