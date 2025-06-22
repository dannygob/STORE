package com.example.store.presentation.ui.viewmodel

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data classes
data class DashboardData(
    val title: String,
    val details: List<String>,
)

data class MenuItem(
    val icon: ImageVector,
    val label: String,
    val action: () -> Unit = {},
)

data class DropdownSection(
    val icon: ImageVector,
    val label: String,
    val options: List<String>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current

    // Extraer datos a funciones separadas para mejor organización
    val dashboardItems = getDashboardItems()
    val menuItems = getMenuItems(context)
    val dropdownSections = getDropdownSections()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Store Dashboard",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Text(
                    text = "Version 1.0",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Menú horizontal mejorado
            HorizontalMenuBar(
                items = menuItems,
                modifier = Modifier.padding(top = 50.dp) // Tu valor optimizado
            )

            // Sección de tarjetas mejorada
            DashboardCardsSection(
                items = dashboardItems,
                modifier = Modifier.weight(1f)
            )

            // Sección de dropdowns mejorada
            DropdownMenusSection(
                sections = dropdownSections,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun HorizontalMenuBar(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                MenuItemComponent(
                    icon = item.icon,
                    label = item.label,
                    onClick = item.action,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MenuItemComponent(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun DashboardCardsSection(
    items: List<DashboardData>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    DashboardCard(
                        data = item,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Espaciador para filas incompletas
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(
    data: DashboardData,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            data.details.forEach { detail ->
                Text(
                    text = "• $detail", // Usar bullet point más elegante
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DropdownMenusSection(
    sections: List<DropdownSection>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        sections.forEach { section ->
            DropdownMenuComponent(
                icon = section.icon,
                label = section.label,
                options = section.options
            )
        }
    }
}

@Composable
private fun DropdownMenuComponent(
    icon: ImageVector,
    label: String,
    options: List<String>,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 4.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelMedium
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand $label",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        Toast.makeText(context, option, Toast.LENGTH_SHORT).show()
                        expanded = false
                    }
                )
            }
        }
    }
}

// Funciones de datos separadas para mejor organización
private fun getDashboardItems() = listOf(
    DashboardData("Order Alerts", listOf("Order #101", "Order #102")),
    DashboardData("Low Stock & Expiration", listOf("Product A low", "Product B expires soon")),
    DashboardData("Sales Statistics", listOf("Today: \$150", "Week: \$1000")),
    DashboardData("Admin Balance", listOf("Income: \$5000", "Expenses: \$2000")),
    DashboardData(
        "Expenses & Services",
        listOf("Water: \$200", "Electricity: \$150", "Internet: \$50")
    ),
    DashboardData("Other Expenses", listOf("Maintenance: \$300", "Stationery: \$50"))
)

private fun getMenuItems(context: android.content.Context) = listOf(
    MenuItem(Icons.Filled.Inventory, "Inventory") {
        Toast.makeText(context, "Inventory", Toast.LENGTH_SHORT).show()
    },
    MenuItem(Icons.Filled.ShoppingCart, "Purchases") {
        Toast.makeText(context, "Purchases", Toast.LENGTH_SHORT).show()
    },
    MenuItem(Icons.Filled.Sell, "Sales") {
        Toast.makeText(context, "Sales", Toast.LENGTH_SHORT).show()
    },
    MenuItem(Icons.Filled.LocalShipping, "Orders") {
        Toast.makeText(context, "Orders", Toast.LENGTH_SHORT).show()
    },
    MenuItem(Icons.Filled.QrCodeScanner, "Scanner") {
        Toast.makeText(context, "Scanner", Toast.LENGTH_SHORT).show()
    },
    MenuItem(Icons.Filled.Payment, "Expenses") {
        Toast.makeText(context, "Expenses", Toast.LENGTH_SHORT).show()
    }
)

private fun getDropdownSections() = listOf(
    DropdownSection(
        Icons.Filled.LocalShipping,
        "Orders",
        listOf("Create Order", "Order Status", "Order Cancellation", "Shipping Notification")
    ),
    DropdownSection(
        Icons.Filled.Add,
        "Products",
        listOf("Add Product", "Product Return")
    ),
    DropdownSection(
        Icons.Filled.Person,
        "People",
        listOf("Add Customer", "Add Supplier", "Add Service Provider")
    )
)

@Preview(showSystemUi = true)
@Composable
fun DashboardPreview() {
    MaterialTheme {
        DashboardScreen()
    }
}