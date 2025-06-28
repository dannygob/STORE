package com.example.store.presentation.orders.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.store.presentation.sales.SalesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: SalesViewModel = viewModel(),
) {
    val products by viewModel.products.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val customerSuggestions by viewModel.customerSuggestions.collectAsState()
    val showConfirmation by viewModel.showConfirmation.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nueva Venta") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (cartItems.isEmpty()) {
                        Toast.makeText(context, "Carrito vacío", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.promptOrderConfirmation()
                    }
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Generar orden")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 1. Productos disponibles
            Text("Productos", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products, key = { it.id }) { product ->
                    DraggableProductCard(product = product, onDrag = {
                        viewModel.onProductDragged(product)
                    })
                }
            }

            Spacer(Modifier.height(24.dp))

            // 2. Carrito drop area
            DropTargetArea(
                isActive = viewModel.isDragInProgress,
                onDrop = { product -> viewModel.addToCart(product) }
            )

            // 3. Carrito expandible
            ExpandableCart(
                items = cartItems,
                onIncrement = viewModel::incrementCartItem,
                onDecrement = viewModel::decrementCartItem
            )

            Spacer(Modifier.height(24.dp))

            // 4. Gestión de cliente
            Text("Cliente", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = selectedCustomer ?: "",
                onValueChange = { viewModel.onCustomerInputChanged(it) },
                label = { Text("Buscar o registrar cliente") },
                modifier = Modifier.fillMaxWidth()
            )

            if (customerSuggestions.isNotEmpty()) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { /* manual dismissal no necesario */ }
                ) {
                    customerSuggestions.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = { viewModel.selectCustomer(name) }
                        )
                    }
                }
            }

            // 5. Confirmación de orden
            if (showConfirmation) {
                AlertDialog(
                    onDismissRequest = { viewModel.cancelConfirmation() },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.confirmOrder()
                            LaunchedEffect(Unit) {
                                snackbarHostState.showSnackbar("Orden generada exitosamente")
                            }
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.cancelConfirmation() }) {
                            Text("Cancelar")
                        }
                    },
                    title = { Text("Confirmar orden") },
                    text = { Text("¿Está seguro de generar esta orden con los productos seleccionados y el cliente actual?") }
                )
            }
        }
    }
}

