package com.example.Store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.Store.presentation.common.navigation.MainNavHost
import dagger.hilt.android.AndroidEntryPoint // 👈 Importación añadida

@AndroidEntryPoint // 👈 Anotación obligatoria para habilitar inyección de dependencias con Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StoreApp()
        }
    }
}

@Composable
fun StoreApp() {
    val navController = rememberNavController() // Inicializa NavController correctamente

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        MainNavHost(navController = navController) // Pasa NavController a MainNavHost
    }
}