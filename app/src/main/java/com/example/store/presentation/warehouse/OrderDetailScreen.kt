package com.example.store.presentation.warehouse

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
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
import com.example.store.presentation.orders.OrderDetailViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    viewModel: OrderDetailViewModel = hiltViewModel(),
    onGoToPickList: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
                    Text("Total: ${order.totalAmount}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        Toast.makeText(context, "Go to Pick List clicked", Toast.LENGTH_SHORT).show()
                        onGoToPickList(order.orderId)
                    }) {
                        Text("Go to Pick List")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Items:", style = MaterialTheme.typography.titleMedium)

                    LazyColumn {
                        items(uiState.orderWithItems!!.items) { item ->
                            ListItem(
                                headlineContent = { Text("Product ID: ${item.productId}") },
                                supportingContent = { Text("Quantity: ${item.quantity} | Price: ${item.price}") }
                            )
                        }
                    }
                }
            }
        }
    }
}
