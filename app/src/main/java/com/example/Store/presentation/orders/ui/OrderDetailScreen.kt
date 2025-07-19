package com.example.Store.presentation.orders.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.store.presentation.orders.OrderDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    viewModel: OrderDetailViewModel = hiltViewModel(),
    onGoToPickList: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Order Details") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.orderWithItems == null) {
                Text("Order not found.", modifier = Modifier.align(Alignment.Center))
            } else {
                val order = uiState.orderWithItems!!.order
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Order ID: ${order.orderId}", style = MaterialTheme.typography.titleLarge)
                    Text("Customer ID: ${order.customerId}")
                    Text("Total: ${order.total}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { onGoToPickList(order.orderId) }) {
                        Text("Go to Pick List")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Items:", style = MaterialTheme.typography.titleMedium)

                    LazyColumn {
                        items(uiState.orderWithItems!!.orderItems) { item ->
                            ListItem(
                                headlineText = { Text("Product ID: ${item.productId}") },
                                supportingText = { Text("Quantity: ${item.quantity} | Price: ${item.unitPrice}") }
                            )
                        }
                    }
                }
            }
        }
    }
}