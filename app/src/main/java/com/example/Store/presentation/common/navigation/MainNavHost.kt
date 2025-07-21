package com.example.Store.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Store.presentation.dashboard.DashboardViewModel
import com.example.Store.presentation.dashboard.ui.DashboardScreen
import com.example.Store.presentation.debug.ui.DebugScreen
import com.example.Store.presentation.expenses.ui.ExpensesScreen
import com.example.Store.presentation.inventory.ui.InventoryItemUi
import com.example.Store.presentation.inventory.ui.InventoryScreen
import com.example.Store.presentation.inventory.ui.InventoryUiState
import com.example.Store.presentation.login.ui.LoginScreen
import com.example.Store.presentation.orders.ui.OrdersScreen
import com.example.Store.presentation.purchases.ui.PurchasesScreen
import com.example.Store.presentation.sales.ui.SalesScreen
import com.example.Store.presentation.scanner.ui.ScannerScreen
import com.example.Store.presentation.splash.ui.SplashScreen

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Dashboard : Route("dashboard")
    object Inventory : Route("inventory")
    object Sales : Route("sales")
    object Debug : Route("debug")
    object Purchases : Route("purchases")
    object Orders : Route("orders")
    object Scanner : Route("scanner")
    object Expenses : Route("expenses")
    object Warehouse : Route("warehouse")
    object OrderDetail : Route("orderDetail/{orderId}") {
        fun createRoute(orderId: String) = "orderDetail/$orderId"
    }
    object OrderPicking : Route("orderPicking/{orderId}") {
        fun createRoute(orderId: String) = "orderPicking/$orderId"
    }
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

        composable(Route.Debug.route) {
            DebugScreen()
        }

        composable(Route.Purchases.route) {
            PurchasesScreen(navController = navController)
        }

        composable(Route.Orders.route) {
            OrdersScreen(navController = navController)
        }

        composable(Route.Scanner.route) {
            ScannerScreen(navController = navController)
        }

        composable(Route.Expenses.route) {
            ExpensesScreen(navController = navController)
        }

        composable(Route.Warehouse.route) {
            WarehouseScreen(navController = navController)
        }

        composable(Route.OrderDetail.route) {
            OrderDetailScreen(
                onGoToPickList = { orderId ->
                    navController.navigate(Route.OrderPicking.createRoute(orderId))
                }
            )
        }

        composable(Route.OrderPicking.route) {
            OrderPickingScreen()
        }
    }
}
