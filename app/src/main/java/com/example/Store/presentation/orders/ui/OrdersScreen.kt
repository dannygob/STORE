package com.example.Store.presentation.orders.ui

import android.Manifest
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.Store.R
import com.example.Store.presentation.sales.CartItem
import com.example.Store.presentation.sales.Customer
import com.example.Store.presentation.sales.Product
import com.example.Store.presentation.sales.SalesViewModel
import com.example.Store.util.PermissionUtils
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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

                Spacer(modifier = Modifier.height(16.dp))
                CameraFeaturePlaceholder(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))
                LocationFeaturePlaceholder(modifier = Modifier.fillMaxWidth())
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
fun CameraFeaturePlaceholder(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val cameraPermissionLauncher = PermissionUtils.rememberPermissionLauncher(
        onPermissionGranted = {
            Toast.makeText(
                context,
                "Camera permission granted. Camera would open here.",
                Toast.LENGTH_SHORT
            ).show()
            // Actual camera intent launch would go here
        },
        onPermissionDenied = { shouldShowRationale ->
            Toast.makeText(
                context,
                "Camera permission denied. Rationale: $shouldShowRationale",
                Toast.LENGTH_LONG
            ).show()
        },
        onPermanentlyDenied = {
            Toast.makeText(
                context,
                "Camera permission permanently denied. Please enable in settings.",
                Toast.LENGTH_LONG
            ).show()
            // Optionally direct to settings
        }
    )
    Button(
        onClick = {
            if (PermissionUtils.isPermissionGranted(context, Manifest.permission.CAMERA)) {
                Toast.makeText(context, "Camera would open here.", Toast.LENGTH_SHORT).show()
                // Actual camera intent launch
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        modifier = modifier
    ) {
        Text("Use Camera (Placeholder)")
    }
}

@Composable
fun LocationFeaturePlaceholder(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locationPermissionLauncher = PermissionUtils.rememberPermissionLauncher(
        onPermissionGranted = {
            Toast.makeText(
                context,
                "Location permission granted. Location would be fetched here.",
                Toast.LENGTH_SHORT
            ).show()
            // Actual location fetching logic would go here
        },
        onPermissionDenied = { shouldShowRationale ->
            Toast.makeText(
                context,
                "Location permission denied. Rationale: $shouldShowRationale",
                Toast.LENGTH_LONG
            ).show()
        },
        onPermanentlyDenied = {
            Toast.makeText(
                context,
                "Location permission permanently denied. Please enable in settings.",
                Toast.LENGTH_LONG
            ).show()
            // Optionally direct to settings
        }
    )
    Button(
        onClick = {
            if (PermissionUtils.isPermissionGranted(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(context, "Location would be fetched here.", Toast.LENGTH_SHORT)
                    .show()
                // Actual location fetching
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        },
        modifier = modifier
    ) {
        Text("Get Location (Placeholder)")
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
    val context = LocalContext.current

    // Launcher for picking a contact
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
        onResult = { contactUri ->
            contactUri?.let { uri ->
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                        val nameIndex =
                            it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                        val contactId = if (idIndex >= 0) it.getString(idIndex) else "N/A"
                        val name = if (nameIndex >= 0) it.getString(nameIndex) else "N/A"

                        var email = "No Email"
                        val emailCursor = context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            arrayOf(contactId),
                            null
                        )
                        emailCursor?.use { ec ->
                            if (ec.moveToFirst()) {
                                val emailAddressIndex =
                                    ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                                if (emailAddressIndex >= 0) email = ec.getString(emailAddressIndex)
                            }
                        }

                        var phoneNumber = "No Phone"
                        val phoneCursor = context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(contactId),
                            null
                        )
                        phoneCursor?.use { pc ->
                            if (pc.moveToFirst()) {
                                val numberIndex =
                                    pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                if (numberIndex >= 0) phoneNumber = pc.getString(numberIndex)
                            }
                        }
                        Toast.makeText(
                            context,
                            "Selected: $name\nPhone: $phoneNumber\nEmail: $email",
                            Toast.LENGTH_LONG
                        ).show()
                        // Here you would typically update the ViewModel or UI state
                        // For example: onCustomerSearchChanged(name)
                        // viewModel.onContactSelected(name, phoneNumber, email) // If such a method exists
                    }
                }
            }
        }
    )

    // Launcher for READ_CONTACTS permission
    val contactsPermissionLauncher = PermissionUtils.rememberPermissionLauncher(
        onPermissionGranted = {
            contactPickerLauncher.launch(null) // No specific URI, launches generic contact picker
        },
        onPermissionDenied = { shouldShowRationale ->
            Toast.makeText(
                context,
                "Contacts permission denied. Rationale: $shouldShowRationale",
                Toast.LENGTH_LONG
            ).show()
        },
        onPermanentlyDenied = {
            Toast.makeText(
                context,
                "Contacts permission permanently denied. Please enable in settings.",
                Toast.LENGTH_LONG
            ).show()
            // Optionally, direct to settings:
            // val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            // val uri = Uri.fromParts("package", context.packageName, null)
            // intent.data = uri
            // context.startActivity(intent)
        }
    )

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

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (PermissionUtils.isPermissionGranted(
                        context,
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    contactPickerLauncher.launch(null)
                } else {
                    contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Customer from Contacts")
        }

        // Placeholder for communication buttons - assuming a contact might have been selected
        // In a real app, these would be enabled/shown based on actual selected contact state
        // and availability of phone/email.
        if (searchQuery.isNotEmpty() || selectedCustomer != null) { // Simplified condition for visibility
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Communication Actions (Placeholder)",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    Toast.makeText(
                        context,
                        "WhatsApp action triggered (placeholder)",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text("WhatsApp")
                }
                Button(onClick = {
                    Toast.makeText(
                        context,
                        "Email action triggered (placeholder)",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text("Email")
                }
                Button(onClick = {
                    Toast.makeText(
                        context,
                        "SMS action triggered (placeholder)",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text("SMS")
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
fun OrdersScreenPreview() {
    MaterialTheme {
        OrdersScreen(
            navController = androidx.navigation.compose.rememberNavController(),
            viewModel = SalesViewModel() // Using real ViewModel, assumes it has reasonable default/mock state
        )
    }
}

