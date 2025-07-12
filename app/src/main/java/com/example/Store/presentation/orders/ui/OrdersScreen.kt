package com.example.Store.presentation.orders.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Store.presentation.orders.OrdersUiState
import com.example.Store.presentation.orders.OrdersViewModel
import com.example.Store.presentation.orders.model.CartItem
import com.example.Store.presentation.orders.model.ProductUi

@Composable
fun OrdersRoute(
    viewModel: OrdersViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var draggedProduct by remember { mutableStateOf<ProductUi?>(null) }

    OrdersScreen(
        uiState = uiState,
        onProductDragged = { product ->
            viewModel.onProductDragged(product)
            draggedProduct = product
        },
        onDrop = {
            draggedProduct?.let { product ->
                viewModel.addToCart(product)
            }
            draggedProduct = null
        },
        onIncrementCartItem = viewModel::incrementCartItem,
        onDecrementCartItem = viewModel::decrementCartItem,
        onCustomerInputChanged = viewModel::onCustomerInputChanged,
        onSelectCustomer = viewModel::selectCustomer,
        onPromptOrderConfirmation = viewModel::promptOrderConfirmation,
        onCancelConfirmation = viewModel::cancelConfirmation,
        onConfirmOrder = viewModel::confirmOrder,
        onUserMessageShown = viewModel::onUserMessageShown
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    uiState: OrdersUiState,
    onProductDragged: (ProductUi) -> Unit,
    onDrop: () -> Unit,
    onIncrementCartItem: (String) -> Unit,
    onDecrementCartItem: (String) -> Unit,
    onCustomerInputChanged: (String) -> Unit,
    onSelectCustomer: (String) -> Unit,
    onPromptOrderConfirmation: () -> Unit,
    onCancelConfirmation: () -> Unit,
    onConfirmOrder: () -> Unit,
    onUserMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            onUserMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create New Order") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (uiState.cartItems.isEmpty()) {
                        Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                    } else {
                        onPromptOrderConfirmation()
                    }
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Generate Order")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Products", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.products, key = { it.id }) { product ->
                    DraggableProductCard(product = product, onDrag = onProductDragged)
                }
            }

            Spacer(Modifier.height(24.dp))

            DropTargetArea(
                isActive = uiState.isDragInProgress,
                onDrop = onDrop
            )

            Spacer(Modifier.height(24.dp))

            ExpandableCart(
                items = uiState.cartItems,
                onIncrement = onIncrementCartItem,
                onDecrement = onDecrementCartItem
            )

            Spacer(Modifier.height(24.dp))

            Text("Customer", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.selectedCustomer ?: "",
                onValueChange = onCustomerInputChanged,
                label = { Text("Search or register customer") },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.customerSuggestions.isNotEmpty()) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { /* manual dismissal not necessary */ }
                ) {
                    uiState.customerSuggestions.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = { onSelectCustomer(name) }
                        )
                    }
                }
            }

            if (uiState.showConfirmation) {
                AlertDialog(
                    onDismissRequest = onCancelConfirmation,
                    confirmButton = {
                        TextButton(onClick = onConfirmOrder) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onCancelConfirmation) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Confirm Order") },
                    text = { Text("Are you sure you want to generate this order with the selected products and customer?") }
                )
            }
        }
    }
}

@Composable
fun DraggableProductCard(product: ProductUi, onDrag: (ProductUi) -> Unit) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    Card(
        modifier = Modifier
            .width(150.dp)
            .pointerInput(product) {
                detectDragGestures(
                    onDragStart = {
                        onDrag(product)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentPosition = currentPosition.plus(dragAmount)
                    },
                    onDragEnd = {
                        // Drop event is handled by the DropTargetArea
                    }
                )
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Image")
            }
            Spacer(Modifier.height(8.dp))
            Text(product.name, fontWeight = FontWeight.Bold)
            Text(product.getFormattedPrice())
            Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DropTargetArea(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onDrop: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(if (isActive) Color.Green.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f))
            .pointerInput(isActive) {
                detectDragGestures(
                    onDragEnd = {
                        if (isActive) {
                            onDrop()
                        }
                    }
                ) { _, _ -> }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Drop Target",
            modifier = Modifier.size(50.dp),
            tint = if (isActive) Color.Green else Color.Gray
        )
    }
}

@Composable
fun ExpandableCart(
    items: List<CartItem>,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            Text("View Cart (${items.sumOf { it.quantity }})")
        }

        if (expanded) {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.product.name} (${item.getFormattedPrice()})")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onDecrement(item.product.id) }) {
                            Icon(Icons.Default.Remove, "Decrement")
                        }
                        Text("${item.quantity}")
                        IconButton(onClick = { onIncrement(item.product.id) }) {
                            Icon(Icons.Default.Add, "Increment")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    MaterialTheme {
        OrdersScreen(
            uiState = OrdersUiState(
                products = listOf(
                    ProductUi(name = "Laptop Pro", stock = 10, price = 1200.00),
                    ProductUi(name = "Smartphone X", stock = 25, price = 800.00),
                ),
                cartItems = listOf(
                    CartItem(ProductUi(name = "Wireless Mouse", stock = 50, price = 25.00), 2)
                ),
                selectedCustomer = "Test Customer"
            ),
            onProductDragged = {},
            onDrop = {},
            onIncrementCartItem = {},
            onDecrementCartItem = {},
            onCustomerInputChanged = {},
            onSelectCustomer = {},
            onPromptOrderConfirmation = {},
            onCancelConfirmation = {},
            onConfirmOrder = {},
            onUserMessageShown = {}
        )
    }
}
