package com.example.store.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.store.presentation.dashboard.DashboardViewModel
import com.example.store.presentation.dashboard.ui.DashboardScreen
import com.example.store.presentation.login.ui.LoginScreen
import com.example.store.presentation.splash.ui.SplashScreen

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Dashboard : Route("dashboard")
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
            val dashboardViewModel: DashboardViewModel = viewModel()
            DashboardScreen(
                navController = navController,
                viewModel = dashboardViewModel
            )
        }
    }
}