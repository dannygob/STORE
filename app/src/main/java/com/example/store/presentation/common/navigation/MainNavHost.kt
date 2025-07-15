package com.example.store.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.store.presentation.debug.ui.DebugScreen
import com.example.store.presentation.location.AddEditLocationScreen
import com.example.store.presentation.inventory.ui.ProductStockManagementScreen
import com.example.store.presentation.location.LocationListScreen
import com.example.store.presentation.location.LocationProductsScreen
import com.example.store.presentation.orders.ui.OrderDetailScreen
import com.example.store.presentation.orders.ui.OrderListScreen
import com.example.store.presentation.picking.OrderPickingScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Dashboard : Route("dashboard")
    object Inventory : Route("inventory")
    object Sales : Route("sales")
    object Debug : Route("debug")
    object LocationList : Route("location_list")
    object AddEditLocation : Route("add_edit_location")
    object LocationProducts : Route("location_products")
    object ProductStockManagement : Route("product_stock_management")
    object OrderPicking : Route("order_picking")
    object OrderList : Route("order_list")
    object OrderDetail : Route("order_detail")
}

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        composable(Route.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Route.Dashboard.route) {
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
            val dashboardViewModel: DashboardViewModel = viewModel()
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

        composable(Route.LocationList.route) {
            LocationListScreen(
                onAddLocationClick = {
                    navController.navigate(Route.AddEditLocation.route) // Navigate without ID for new
                },
                onLocationClick = { locationId ->
                    navController.navigate("${Route.LocationProducts.route}/$locationId")
                }
            )
        }

        composable(
            route = "${Route.LocationProducts.route}/{locationId}",
            arguments = listOf(navArgument("locationId") { type = NavType.StringType })
        ) {
            LocationProductsScreen()
        }

        composable(
            route = Route.AddEditLocation.route + "?locationId={locationId}",
            arguments = listOf(navArgument("locationId") {
                defaultValue = null
                nullable = true
                type = NavType.StringType
            })
        ) {
            AddEditLocationScreen(
                onSaveFinished = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Route.ProductStockManagement.route}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) {
            ProductStockManagementScreen(
                onSaveFinished = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Route.OrderPicking.route}/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            OrderPickingScreen()
        }

        composable(Route.OrderList.route) {
            OrderListScreen(
                onOrderClick = { orderId ->
                    navController.navigate("${Route.OrderDetail.route}/$orderId")
                }
            )
        }

        composable(
            route = "${Route.OrderDetail.route}/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            OrderDetailScreen(
                onGoToPickList = { orderId ->
                    navController.navigate("${Route.OrderPicking.route}/$orderId")
                }
            )
        }
    }
}
