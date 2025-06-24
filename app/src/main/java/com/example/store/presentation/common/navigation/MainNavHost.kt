package com.example.store.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.store.presentation.dashboard.ui.DashboardScreen
import com.example.store.presentation.login.ui.LoginScreen
import com.example.store.presentation.splash.ui.SplashScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("login") {
                        launchSingleTop = true
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                // viewModel is provided by default from hiltViewModel or viewModel()
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        launchSingleTop = true
                        popUpTo("login") { inclusive = true } // Pop login from back stack
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}
