package com.example.store.presentation.orders.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.presentation.orders.OrdersViewModel
import com.example.store.presentation.orders.model.OrderItemUi
import com.example.store.presentation.orders.model.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onUserMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Orders") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.createNewOrderPlaceholder()
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Create New Order")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders found.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.orders, key = { it.id }) { order ->
                        OrderListItem(
                            order = order,
                            onItemClick = { viewModel.viewOrderDetailsPlaceholder(order.id) },
                            onStatusUpdate = { newStatus -> viewModel.updateOrderStatusPlaceholder(order.id, newStatus)}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderListItem(
    order: OrderItemUi,
    onItemClick: () -> Unit,
    onStatusUpdate: (OrderStatus) -> Unit // Callback to update status
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    order.orderNumber,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                StatusIndicator(status = order.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodyMedium)
            Text("Date: ${order.getFormattedDate()}", style = MaterialTheme.typography.bodySmall)
            Text("Summary: ${order.itemSummary}", style = MaterialTheme.typography.bodySmall, maxLines = 2)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total: ${order.getFormattedTotalAmount()}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Box {
                    IconButton(onClick = { showStatusMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Update status for ${order.orderNumber}")
                    }
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        OrderStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.getDisplayValue()) },
                                onClick = {
                                    onStatusUpdate(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(status: OrderStatus) {
    val backgroundColor = when (status) {
        OrderStatus.PENDING -> Color.LightGray
        OrderStatus.PROCESSING -> Color(0xFFFFF9C4) // Light Yellow
        OrderStatus.SHIPPED -> Color(0xFFBBDEFB) // Light Blue
        OrderStatus.DELIVERED -> Color(0xFFC8E6C9) // Light Green
        OrderStatus.CANCELED -> Color(0xFFFFCDD2) // Light Red
    }
    val textColor = when (status) {
        OrderStatus.PENDING -> Color.DarkGray
        OrderStatus.PROCESSING -> Color(0xFF795548) // Brownish
        OrderStatus.SHIPPED -> Color(0xFF0D47A1) // Dark Blue
        OrderStatus.DELIVERED -> Color(0xFF1B5E20) // Dark Green
        OrderStatus.CANCELED -> Color(0xFFB71C1C) // Dark Red
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.getDisplayValue(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}
