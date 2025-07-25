package com.example.store.presentation.dashboard

import app.cash.turbine.test
import com.example.store.presentation.dashboard.model.NotificationItemUi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel()
        testDispatcher.scheduler.runCurrent() // Process initial loadNotifications
    }

    @Test
    fun `initial state loads mock notifications and updates unread count`() = runTest {
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.isLoadingNotifications).isFalse()
            assertThat(emission.notifications).isNotEmpty()
            val expectedUnread = emission.notifications.count { !it.isRead }
            assertThat(emission.unreadNotificationCount).isEqualTo(expectedUnread)
            // Check if sorted by date descending
            if (emission.notifications.size > 1) {
                for (i in 0 until emission.notifications.size - 1) {
                    assertThat(emission.notifications[i].timestamp)
                        .isAtLeast(emission.notifications[i+1].timestamp)
                }
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markAsRead updates notification and unread count`() = runTest {
        // Find an unread notification from the initial mock load
        val unreadNotification = viewModel.uiState.value.notifications.firstOrNull { !it.isRead }
        assertThat(unreadNotification).isNotNull()
        val initialUnreadCount = viewModel.uiState.value.unreadNotificationCount

        unreadNotification?.let {
            viewModel.markAsRead(it.id)
            viewModel.uiState.test {
                val emission = awaitItem()
                val updatedNotification = emission.notifications.find { n -> n.id == it.id }
                assertThat(updatedNotification?.isRead).isTrue()
                assertThat(emission.unreadNotificationCount).isEqualTo(initialUnreadCount - 1)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `markAsRead on already read notification does not change count`() = runTest {
        val readNotification = viewModel.uiState.value.notifications.firstOrNull { it.isRead }
        assertThat(readNotification).isNotNull() // Assuming mock data has at least one read
        val initialUnreadCount = viewModel.uiState.value.unreadNotificationCount

        readNotification?.let {
            viewModel.markAsRead(it.id) // Attempt to mark as read again
             viewModel.uiState.test { // State shouldn't have changed in a way that triggers new emission if no actual change
                val emission = awaitItem()
                assertThat(emission.unreadNotificationCount).isEqualTo(initialUnreadCount)
                // No user message for this specific action in VM, so not checking that
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `markAllAsRead updates all notifications and unread count, sets message`() = runTest {
        // Ensure there's at least one unread notification
        val hasUnread = viewModel.uiState.value.notifications.any { !it.isRead }
        if (!hasUnread && viewModel.uiState.value.notifications.isNotEmpty()) {
            // If all are read, this test might not be as effective, but let's proceed
            // Or, modify one to be unread for test setup if needed.
        }

        viewModel.markAllAsRead()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.notifications.all { it.isRead }).isTrue()
            assertThat(emission.unreadNotificationCount).isEqualTo(0)
            if (hasUnread || viewModel.uiState.value.notifications.isEmpty()) { // Check message only if action was meaningful or list was empty
                 assertThat(emission.userMessage).isEqualTo("All notifications marked as read.")
            } else { // If all were already read
                 assertThat(emission.userMessage).isEqualTo("All notifications already read.")
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markAllAsRead when already all read sets appropriate message`() = runTest {
        // Make all notifications read first
        val allReadNotifications = viewModel.uiState.value.notifications.map { it.copy(isRead = true) }
        viewModel._uiState.update { // Direct update for test setup
            it.copy(notifications = allReadNotifications, unreadNotificationCount = 0)
        }

        viewModel.markAllAsRead()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("All notifications already read.")
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `dismissNotification removes notification and updates unread count, sets message`() = runTest {
        val notificationToDismiss = viewModel.uiState.value.notifications.firstOrNull()
        assertThat(notificationToDismiss).isNotNull()
        val initialUnreadCount = viewModel.uiState.value.unreadNotificationCount
        val initialSize = viewModel.uiState.value.notifications.size

        var expectedUnreadAfterDismiss = initialUnreadCount
        if (notificationToDismiss != null && !notificationToDismiss.isRead) {
            expectedUnreadAfterDismiss--
        }

        notificationToDismiss?.let {
            viewModel.dismissNotification(it.id)
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission.notifications.size).isEqualTo(initialSize - 1)
                assertThat(emission.notifications.find { n -> n.id == it.id }).isNull()
                assertThat(emission.unreadNotificationCount).isEqualTo(expectedUnreadAfterDismiss)
                assertThat(emission.userMessage).isEqualTo("Notification '${it.title}' dismissed.")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `dismissAllNotifications clears list and unread count, sets message`() = runTest {
        // Ensure there are notifications to dismiss
         if (viewModel.uiState.value.notifications.isEmpty()) {
            // Add a dummy notification for test if needed, or rely on init
            viewModel._uiState.update { it.copy(notifications = listOf(NotificationItemUi(title="Test", message="Test msg"))) }
        }

        viewModel.dismissAllNotifications()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.notifications).isEmpty()
            assertThat(emission.unreadNotificationCount).isEqualTo(0)
            assertThat(emission.userMessage).isEqualTo("All notifications dismissed.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dismissAllNotifications when already empty sets appropriate message`() = runTest {
        viewModel._uiState.update { it.copy(notifications = emptyList(), unreadNotificationCount = 0) } // Ensure empty state

        viewModel.dismissAllNotifications()
        viewModel.uiState.test {
            val emission = awaitItem()
            assertThat(emission.userMessage).isEqualTo("No notifications to dismiss.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshNotifications reloads data and sets message`() = runTest {
        // This test assumes refresh just reloads mock data for now
        // It will primarily check the userMessage and that loading state toggles
        viewModel.refreshNotifications()
        viewModel.uiState.test {
            // First emission might be the userMessage for "Refreshing..."
            var emission = awaitItem()
            if (emission.userMessage == "Refreshing notifications... (Placeholder)") {
                assertThat(emission.isLoadingNotifications).isFalse() // Should be false before actual load starts in refresh
                emission = awaitItem() // Then loading true
            }
            assertThat(emission.isLoadingNotifications).isTrue()

            emission = awaitItem() // Then loading false with new data
            assertThat(emission.isLoadingNotifications).isFalse()
            assertThat(emission.notifications).isNotEmpty() // Assuming mock data is always non-empty
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onUserMessageShown clears userMessage`() = runTest {
        viewModel._uiState.update { it.copy(userMessage = "A test message") } // Set a message directly for test
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
