package com.example.Store

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.Store.presentation.common.navigation.ScreenRoutes
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DashboardNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = rememberNavController()
            StoreApp() // Assuming StoreApp sets up the NavHost with Dashboard
        }
        // Navigate past Splash and Login to reach Dashboard
        // This requires some assumptions about your Splash and Login screen behavior.
        // For a real app, you might use test rules or specific test doubles for ViewModel/Auth.

        // Simple navigation:
        // 1. Wait for Splash to finish (assuming it navigates to Login)
        // This is a placeholder, actual waiting mechanism might be needed if splash has a delay.
        // composeTestRule.waitUntil(timeoutMillis = 5000) {
        // navController.currentDestination?.route == ScreenRoutes.LOGIN
        // }
        // If there's no automatic navigation from splash for testing, manually navigate:
        // Manually navigate to Login if not already there (e.g. if splash is instant)
        // This is tricky without knowing exact splash/login logic.
        // For now, assume we can directly navigate for test setup or that login is quick.

        // Perform Login
        composeTestRule.onNodeWithText("Username").performTextInput("user")
        composeTestRule.onNodeWithText("Password").performTextInput("password")
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for navigation to Dashboard
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            navController.currentDestination?.route == ScreenRoutes.DASHBOARD
        }
    }

    private fun navigateToDashboardAndAssert(
        menuItemText: String,
        expectedRoute: String
    ) {
        // Ensure we are on the dashboard first (e.g., after another test)
        // This is a simplified check. A more robust way would be to check for a unique element on the dashboard.
        if (navController.currentDestination?.route != ScreenRoutes.DASHBOARD) {
            // Try to go back or re-navigate. This part can be complex.
            // For simplicity, this test suite assumes tests run independently or setup handles this.
            // Re-login might be needed if state is not preserved.
            // This setup is basic and might need refinement based on app's behavior.
            composeTestRule.onNodeWithText("Username").performTextInput("user")
            composeTestRule.onNodeWithText("Password").performTextInput("password")
            composeTestRule.onNodeWithText("Login").performClick()
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                navController.currentDestination?.route == ScreenRoutes.DASHBOARD
            }
        }

        composeTestRule.onNodeWithText(menuItemText).assertIsDisplayed()
        composeTestRule.onNodeWithText(menuItemText).performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            navController.currentDestination?.route == expectedRoute
        }
        // Assert current route is the expected one
        assert(navController.currentDestination?.route == expectedRoute) {
            "Failed to navigate to $expectedRoute. Current route: ${navController.currentDestination?.route}"
        }

        // Navigate back to Dashboard for the next test
        navController.popBackStack()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            navController.currentDestination?.route == ScreenRoutes.DASHBOARD
        }
    }

    @Test
    fun clickInventory_navigatesToInventoryScreen() {
        navigateToDashboardAndAssert("Inventory", ScreenRoutes.INVENTORY)
    }

    @Test
    fun clickPurchases_navigatesToPurchasesScreen() {
        navigateToDashboardAndAssert("Purchases", ScreenRoutes.PURCHASES)
    }

    @Test
    fun clickSales_navigatesToSalesScreen() {
        navigateToDashboardAndAssert("Sales", ScreenRoutes.SALES)
    }

    @Test
    fun clickOrders_navigatesToOrdersScreen() {
        navigateToDashboardAndAssert("Orders", ScreenRoutes.ORDERS)
    }

    @Test
    fun clickScanner_navigatesToScannerScreen() {
        navigateToDashboardAndAssert("Scanner", ScreenRoutes.SCANNER)
    }

    @Test
    fun clickExpenses_navigatesToExpensesScreen() {
        navigateToDashboardAndAssert("Expenses", ScreenRoutes.EXPENSES)
    }
}
