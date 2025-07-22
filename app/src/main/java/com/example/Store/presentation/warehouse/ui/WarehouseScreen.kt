package com.example.Store.presentation.warehouse.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.Store.presentation.common.navigation.Route
import com.example.Store.presentation.warehouse.WarehouseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseScreen(
    navController: NavController,
    viewModel: WarehouseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Warehouse Orders") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.orders) { orderWithItems ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        "Order ${orderWithItems.order.orderId} clicked",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(
                                        Route.OrderDetail.createRoute(
                                            orderWithItems.order.orderId
                                        )
                                    )
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Order ID: ${orderWithItems.order.orderId}", style = MaterialTheme.typography.titleMedium)
                                Text("Customer ID: ${orderWithItems.order.customerId}")
                                Text("Total: ${orderWithItems.order.totalAmount}")
                            }
                        }
                    }
                }
            }
        }
    }
}
