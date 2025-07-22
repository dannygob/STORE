package com.example.Store.presentation.orders.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.Store.presentation.orders.OrderListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    viewModel: OrderListViewModel = hiltViewModel(),
    onOrderClick: (String) -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("All Orders") })
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(orders) { order ->
                ListItem(
                    headlineContent = { Text("Order #${order.orderId}") },
                    supportingContent = {
                        Text("Customer: ${order.customerId} - Total: ${order.totalAmount}")
                    },
                    trailingContent = {
                        Text(dateFormatter.format(Date(order.orderDate)))
                    },
                    modifier = Modifier.clickable { onOrderClick(order.orderId) }
                )
            }
        }
    }
}
