package com.example.store.presentation.navigation

import DashboardScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.store.presentation.viewmodels.SplashScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("dashboard") {
                        launchSingleTop = true
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}
