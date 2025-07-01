package com.example.store.presentation.sales.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.store.R
import com.example.store.presentation.sales.CartItem
import com.example.store.presentation.sales.Customer
import com.example.store.presentation.sales.Product
import com.example.store.presentation.sales.SalesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    navController: NavController,
    viewModel: SalesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var cartPosition by remember { mutableStateOf(Offset.Zero) }
    var showCartDetails by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onUserMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Point of Sale") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Product Carousel
                ProductCarousel(
                    products = uiState.inventory,
                    onProductDrag = { productId, dragAmount ->
                        // This is a simplified drag simulation.
                        // A real implementation would use a Drag-and-Drop library.
                    },
                    onProductDrop = { productId, dropPosition ->
                        if (cartPosition.y > 0 && dropPosition.y > cartPosition.y) {
                            viewModel.onProductDraggedToCart(productId)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Shopping Cart Icon
                ShoppingCart(
                    cartItems = uiState.cart,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .onGloballyPositioned { coordinates ->
                            cartPosition = coordinates.positionInWindow()
                        },
                    onCartClick = { showCartDetails = !showCartDetails }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Customer Section
                CustomerSection(
                    searchQuery = uiState.customerSearchQuery,
                    newCustomerName = uiState.newCustomerName,
                    customers = uiState.customers,
                    selectedCustomer = uiState.selectedCustomer,
                    onSearchQueryChanged = viewModel::onCustomerSearchChanged,
                    onNewCustomerNameChanged = viewModel::onNewCustomerNameChanged,
                    onSelectCustomer = viewModel::selectCustomer,
                    onRegisterCustomer = viewModel::registerNewCustomer
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Generate Order Button
                Button(
                    onClick = viewModel::onGenerateOrderClicked,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.cart.isNotEmpty() && uiState.selectedCustomer != null
                ) {
                    Text("Generate Order (${String.format(Locale.US, "%.2f", uiState.cartTotal)})")
                }
            }

            // Cart Details (Modal Bottom Sheet)
            if (showCartDetails) {
                ModalBottomSheet(onDismissRequest = { showCartDetails = false }) {
                    CartDetails(
                        cartItems = uiState.cart,
                        onIncrement = viewModel::incrementCartItem,
                        onDecrement = viewModel::decrementCartItem
                    )
                }
            }

            // Confirmation Dialog
            if (uiState.showConfirmDialog) {
                ConfirmOrderDialog(
                    onConfirm = viewModel::confirmOrderGeneration,
                    onDismiss = viewModel::dismissConfirmDialog
                )
            }
        }
    }
}

@Composable
fun ProductCarousel(
    products: List<Product>,
    onProductDrag: (String, Offset) -> Unit,
    onProductDrop: (String, Offset) -> Unit,
) {
    Column {
        Text("Products", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onDrag = { dragAmount -> onProductDrag(product.id, dragAmount) },
                    onDrop = { dropPosition -> onProductDrop(product.id, dropPosition) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onDrag: (Offset) -> Unit,
    onDrop: (Offset) -> Unit,
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    Card(
        modifier = Modifier
            .width(150.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset = Offset.Zero },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onDrop(offset) }
                )
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.placeholder_image_product),
                contentDescription = product.name,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(12.dp)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Text("$${product.price}", style = MaterialTheme.typography.bodyMedium)
                Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCart(
    cartItems: List<CartItem>,
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
) {
    BadgedBox(
        badge = {
            if (cartItems.isNotEmpty()) {
                Badge { Text("${cartItems.sumOf { it.quantity }}") }
            }
        },
        modifier = modifier
    ) {
        IconButton(
            onClick = onCartClick,
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Shopping Cart",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun CartDetails(
    cartItems: List<CartItem>,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("Cart Details", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        if (cartItems.isEmpty()) {
            Text("Cart is empty", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            cartItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.product.name} ($${item.product.price})")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onDecrement(item.product.id) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Remove one")
                        }
                        Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { onIncrement(item.product.id) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add one")
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSection(
    searchQuery: String,
    newCustomerName: String,
    customers: List<Customer>,
    selectedCustomer: Customer?,
    onSearchQueryChanged: (String) -> Unit,
    onNewCustomerNameChanged: (String) -> Unit,
    onSelectCustomer: (Customer) -> Unit,
    onRegisterCustomer: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredCustomers = customers.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column {
        Text("Customer", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                label = { Text("Search or Select Customer") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable), // Updated menuAnchor
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded && filteredCustomers.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                filteredCustomers.forEach { customer ->
                    DropdownMenuItem(
                        text = { Text(customer.name) },
                        onClick = {
                            onSelectCustomer(customer)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (selectedCustomer == null && searchQuery.isNotEmpty() && filteredCustomers.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newCustomerName,
                    onValueChange = onNewCustomerNameChanged,
                    label = { Text("New Customer Name") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRegisterCustomer) {
                    Text("Register")
                }
            }
        }
    }
}

@Composable
fun ConfirmOrderDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Order") },
        text = { Text("Are you sure you want to generate the order with these products and this customer?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SalesScreenPreview() {
    androidx.compose.material3.MaterialTheme {
        SalesScreen(
            navController = androidx.navigation.compose.rememberNavController(),
            viewModel = SalesViewModel() // Using real ViewModel, assumes it has reasonable default/mock state
        )
    }
}
