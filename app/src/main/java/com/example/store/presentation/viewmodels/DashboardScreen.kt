package com.example.store.presentation.viewmodels

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val dashboardItems = remember {
        listOf(
            DashboardData("Alertas de pedidos", listOf("Pedido #101", "Pedido #102")),
            DashboardData(
                "Bajo stock y vencimiento",
                listOf("Producto A bajo", "Producto B vence pronto")
            ),
            DashboardData("Estadísticas de ventas", listOf("Hoy: $150", "Semana: $1000")),
            DashboardData("Balance administrativo", listOf("Ingresos: $5000", "Egresos: $2000")),
            DashboardData(
                "Gastos y Servicios",
                listOf("Agua: $200", "Electricidad: $150", "Internet: $50")
            ),
            DashboardData("Otros gastos", listOf("Mantenimiento: $300", "Papelería: $50"))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store Dashboard") },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        },
        bottomBar = {
            BottomAppBar {
                Text(
                    text = "Versión 1.0",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Menú lateral
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DashboardIcon(Icons.Filled.Inventory, "Inventario") {
                    Toast.makeText(context, "Control de inventario", Toast.LENGTH_SHORT).show()
                }
                DashboardIcon(Icons.Filled.ShoppingCart, "Compras") {
                    Toast.makeText(context, "Compras", Toast.LENGTH_SHORT).show()
                }
                DashboardIcon(Icons.Filled.Sell, "Ventas") {
                    Toast.makeText(context, "Ventas", Toast.LENGTH_SHORT).show()
                }
                DashboardIcon(Icons.Filled.LocalShipping, "Pedidos") {
                    Toast.makeText(context, "Pedidos", Toast.LENGTH_SHORT).show()
                }
                DashboardIcon(Icons.Filled.QrCodeScanner, "Escáner") {
                    Toast.makeText(context, "Verificador de precios", Toast.LENGTH_SHORT).show()
                }
                DashboardIcon(Icons.Filled.Payment, "Gastos y Servicios") {
                    Toast.makeText(context, "Gastos y Servicios", Toast.LENGTH_SHORT).show()
                }
            }

            // Contenido principal con desplazamiento dinámico en dos columnas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(dashboardItems.chunked(2)) { rowItems ->
                    DashboardRow(rowItems)
                }
            }
        }
    }
}

@Composable
fun DashboardIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
fun DashboardRow(cards: List<DashboardData>) {
    LazyRow(
        modifier = Modifier.wrapContentHeight(), // Evita restricciones infinitas de altura
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(cards) { cardData ->
            DashboardCard(title = cardData.title, items = cardData.items)
        }
    }
}

@Composable
fun DashboardCard(title: String, items: List<String>) = Card(
    modifier = Modifier
        .width(180.dp)
        .heightIn(min = 160.dp, max = 400.dp) // Evita restricciones infinitas de altura
        .padding(8.dp),
    shape = MaterialTheme.shapes.medium,
    elevation = CardDefaults.cardElevation(4.dp)
) {
    Column(modifier = Modifier.padding(12.dp)) {
        Text(text = title, fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(items) { item ->
                Text(text = "• $item", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// Modelo de datos para manejar tarjetas dinámicas
data class DashboardData(val title: String, val items: List<String>)
