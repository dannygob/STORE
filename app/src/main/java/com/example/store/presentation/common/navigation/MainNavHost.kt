package com.example.store.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.store.presentation.dashboard.ui.DashboardScreen // Ensured correct name
import com.example.store.presentation.expenses.ui.ExpensesScreen
import com.example.store.presentation.inventory.ui.InventoryScreen
import com.example.store.presentation.login.ui.LoginScreen
import com.example.store.presentation.orders.ui.OrdersScreen
import com.example.store.presentation.purchases.ui.PurchasesScreen
import com.example.store.presentation.scanner.ui.ScannerScreen
import com.example.store.presentation.sales.ui.SalesScreen
import com.example.store.presentation.splash.ui.SplashScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.SPLASH
    ) {
        composable(ScreenRoutes.SPLASH) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(ScreenRoutes.LOGIN) {
                        launchSingleTop = true
                        popUpTo(ScreenRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(ScreenRoutes.LOGIN) {
            LoginScreen(
                // viewModel is provided by default from hiltViewModel or viewModel()
                onLoginSuccess = {
                    navController.navigate(ScreenRoutes.DASHBOARD) {
                        launchSingleTop = true
                        popUpTo(ScreenRoutes.LOGIN) { inclusive = true } // Pop login from back stack
                    }
                }
            )
        }
        composable(ScreenRoutes.DASHBOARD) {
            // Ensure you are using the correct name for DashboardScreen,
            // If it was DashboadSceern.kt, the import and call should match that.
            // For now, assuming it's DashboardScreen or will be renamed.
            DashboardScreen(navController = navController)
        }
        composable(ScreenRoutes.SALES) {
            SalesScreen(navController = navController)
        }
        composable(ScreenRoutes.INVENTORY) { // Changed from PRODUCTS
            InventoryScreen(navController = navController)
        }
        composable(ScreenRoutes.PURCHASES) {
            PurchasesScreen(navController = navController)
        }
        composable(ScreenRoutes.ORDERS) {
            OrdersScreen(navController = navController)
        }
        composable(ScreenRoutes.SCANNER) {
            ScannerScreen(navController = navController)
        }
        composable(ScreenRoutes.EXPENSES) {
            ExpensesScreen(navController = navController)
        }
        // Removed composables for CATEGORIES, CUSTOMERS, REPORTS
    }
}
