package com.example.store.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.store.presentation.dashboard.DashboardViewModel
import com.example.store.presentation.dashboard.ui.DashboardScreen
import com.example.store.presentation.inventory.ui.InventoryItemUi
import com.example.store.presentation.inventory.ui.InventoryScreen
import com.example.store.presentation.inventory.ui.InventoryUiState
import com.example.store.presentation.login.ui.LoginScreen
import com.example.store.presentation.sales.ui.SalesScreen
import com.example.store.presentation.splash.ui.SplashScreen
import com.example.store.presentation.debug.ui.DebugScreen // Moved Import

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Dashboard : Route("dashboard")
    object Inventory : Route("inventory")
    object Sales : Route("sales")
    object Debug : Route("debug") // New Route
}

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        composable(Route.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Dashboard.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                navController = navController,
                viewModel = dashboardViewModel
            )
        }

        composable(Route.Inventory.route) {
            val items = listOf(
                InventoryItemUi("1", "Leche Entera", 5, 1.25, "Lácteos"),
                InventoryItemUi("2", "Arroz", 0, 0.85, "Granos"),
                InventoryItemUi("3", "Huevos", 2, 0.25, "Proteínas")
            )
            InventoryScreen(
                navController = navController,
                state = InventoryUiState(
                    items = items,
                    filteredItems = items
                ),
                onSearchChanged = {},
                onTabSelected = {}
            )
        }

        composable(Route.Sales.route) {
            SalesScreen(navController = navController)
        }

        composable(Route.Debug.route) { // Composable for DebugScreen
            DebugScreen()
        }

    }
}
