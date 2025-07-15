package com.example.store.presentation.splash.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator // Added for loading
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.store.R
import com.example.store.presentation.auth.AuthViewModel
import kotlinx.coroutines.delay

// Data class to hold auth status and loading state
private data class AuthState(val isAuthenticated: Boolean? = null)

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var visible by remember { mutableStateOf(false) }
    // Observe the isAuthenticated flow from AuthViewModel
    // Collect isAuthenticated state
    val isAuthenticatedState by authViewModel.isAuthenticated.collectAsState()
    // Represents whether the auth check is complete and we have a definitive state
    var authCheckCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true // Start animations
        // Wait for a minimum display time OR auth check to complete, whichever is longer
        // This ensures splash is shown for at least a short period.
        delay(1000) // Minimum splash display time (adjust as needed)
        authCheckCompleted = true // Mark that minimum display time is over
    }

    // This effect triggers navigation once auth state is known AND splash has been visible for a minimum time
    LaunchedEffect(isAuthenticatedState, authCheckCompleted) {
        if (authCheckCompleted) {
            // A short delay to ensure UI elements (like fade out) can complete if needed
            // And to ensure isAuthenticatedState is stable from the flow
            delay(500) // Small delay to ensure state is settled
            if (isAuthenticatedState) {
                onNavigateToDashboard()
            } else {
                onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedVisibility(visible = visible, enter = fadeIn()) {
                Image(
                    painter = painterResource(id = R.drawable.store_watermark),
                    contentDescription = "Store Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                )
            }

            AnimatedVisibility(visible = visible, enter = fadeIn()) {
                Text(
                    text = "Welcome to Our Store!",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Optional: Show a progress indicator while waiting for auth state after initial animations
            AnimatedVisibility(visible = visible && !authCheckCompleted, enter = fadeIn()) {
                 CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(
            onNavigateToLogin = {},
            onNavigateToDashboard = {}
            // Preview won't have a real AuthViewModel, so this will show the initial state
        )
    }
}