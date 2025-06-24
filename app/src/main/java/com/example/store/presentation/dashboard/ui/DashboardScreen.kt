package com.example.store.presentation.dashboard.ui

import android.content.Context
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
import androidx.compose.material.icons.filled.Inventory // For Inventory
import androidx.compose.material.icons.filled.LocalShipping // For Orders
import androidx.compose.material.icons.filled.Payment // For Expenses
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner // For Scanner
import androidx.compose.material.icons.filled.Sell // For Sales
import androidx.compose.material.icons.filled.ShoppingCart // For Purchases
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.store.presentation.common.navigation.ScreenRoutes

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
fun DashboardScreen(navController: NavController) { // Added NavController
    val context = LocalContext.current

    // Extraer datos a funciones separadas para mejor organización
    val dashboardItems = getDashboardItems()
    // Pass NavController to getMenuItems
    val menuItems = getMenuItems(context, navController)
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

            // Charts Placeholder Section
            ChartsSectionPlaceholder(
                modifier = Modifier.padding(horizontal = 16.dp) // Match horizontal padding of other sections
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
import androidx.compose.material.icons.filled.MoreVert

@Composable
private fun DashboardCard(
    data: DashboardData,
    modifier: Modifier = Modifier,
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current // For Toasts

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp) // Adjusted padding slightly
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f) // Allow title to take space
                )
                // Box to anchor the DropdownMenu
                Box {
                    IconButton(onClick = { showDropdownMenu = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options for ${data.title}",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showDropdownMenu,
                        onDismissRequest = { showDropdownMenu = false }
                    ) {
                        // Define placeholder titles
                        val placeholderTitles = setOf("Upcoming Tasks", "General Reminders", "System Health", "Customer Insights") // Add any other titles that are placeholders

                        if (data.title in placeholderTitles || data.details.isEmpty()) {
                            // Placeholder actions for specific cards or if details are empty
                            val placeholderActions = listOf("Placeholder Action 1", "Placeholder Action 2", "Placeholder Action 3")
                            placeholderActions.forEach { actionText ->
                                DropdownMenuItem(
                                    text = { Text(actionText) },
                                    onClick = {
                                        Toast.makeText(context, "$actionText selected.", Toast.LENGTH_SHORT).show()
                                        showDropdownMenu = false
                                    }
                                )
                            }
                        } else {
                            // Real details for other cards
                            data.details.forEach { detail ->
                                DropdownMenuItem(
                                    text = { Text(detail) },
                                    onClick = {
                                        Toast.makeText(context, "Selected: $detail", Toast.LENGTH_SHORT).show()
                                        showDropdownMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Maintain some spacing
            Text(
                text = if (data.details.isNotEmpty() || (data.title in setOf("Upcoming Tasks", "General Reminders", "System Health", "Customer Insights")))
                           "Click the icon for details/actions."
                       else
                           "No details available.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
            )

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
    DashboardData("Order Alerts", listOf(
        "Order #101 - Pending Shipment",
        "Order #102 - Payment Due",
        "Order #103 - Ready for Pickup",
        "Order #104 - New Inquiry"
    )),
    DashboardData("Low Stock & Expiration", listOf(
        "Product A - Low Stock (5 remaining)",
        "Product B - Expires Soon (2 days)",
        "Product C - Out of Stock",
        "Product D - Expires Today"
    )),
    DashboardData("Sales Statistics", listOf(
        "Today's Sales: \$150.75 (5 transactions)",
        "Week to Date: \$1050.20 (32 transactions)",
        "Month to Date: \$4500.60 (120 transactions)",
        "Top Product: Product X"
    )),
    DashboardData("Admin Balance", listOf(
        "Account A: \$5000.00",
        "Account B: \$2345.10 (Overdraft)",
        "Pending Transfers: \$300.00",
        "Last Statement: 01 Nov 2023"
    )),
    DashboardData(
        "Expenses & Services",
        listOf(
            "Water Bill: \$200 (Due 15th)",
            "Electricity: \$150 (Paid)",
            "Internet: \$50 (Due 10th)",
            "Cleaning Service: \$120 (Scheduled Fri)"
        )
    ),
    DashboardData("Other Expenses", listOf(
        "Maintenance: \$300 (Repair AC)",
        "Stationery: \$50 (Pens, Paper)",
        "Software Subscription: \$25 (Monthly)",
        "Unexpected Repair: \$150 (Freezer)"
    )),
    DashboardData("Customer Insights", listOf(
        "New Customers This Week: 5",
        "Top Customer: Jane Doe (\$500 spend)",
        "Recent Feedback: Positive (Order #103)",
        "Loyalty Program Members: 120"
    )),
    DashboardData("System Health", listOf(
        "Backup Status: Successful (Today 03:00)",
        "API Latency: 250ms (Normal)",
        "Disk Space: 75% Used (Server 1)",
        "Security Alerts: 0"
    )),
    // Adding two more to make it 10 items for better scrolling
    DashboardData("Upcoming Tasks", listOf(
        "Follow up with Supplier X",
        "Schedule staff meeting",
        "Plan holiday promotion",
        "Renew business license"
    )),
    DashboardData("General Reminders", listOf(
        "Bank holiday next Monday",
        "Submit tax forms by EOM",
        "Check email for new regulations",
        "Water plants"
    ))
)

// Updated getMenuItems to accept NavController and use ScreenRoutes
private fun getMenuItems(context: Context, navController: NavController) = listOf(
    MenuItem(Icons.Filled.Inventory, "Inventory") {
        navController.navigate(ScreenRoutes.INVENTORY)
    },
    MenuItem(Icons.Filled.ShoppingCart, "Purchases") {
        navController.navigate(ScreenRoutes.PURCHASES)
    },
    MenuItem(Icons.Filled.Sell, "Sales") {
        navController.navigate(ScreenRoutes.SALES)
    },
    MenuItem(Icons.Filled.LocalShipping, "Orders") {
        navController.navigate(ScreenRoutes.ORDERS)
    },
    MenuItem(Icons.Filled.QrCodeScanner, "Scanner") {
        navController.navigate(ScreenRoutes.SCANNER)
    },
    MenuItem(Icons.Filled.Payment, "Expenses") {
        navController.navigate(ScreenRoutes.EXPENSES)
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

@Composable
private fun ChartsSectionPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Add some vertical spacing around this section
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp) // Give it a noticeable height
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Charts Area - Coming Soon",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DashboardPreview() {
    MaterialTheme {
        // Provide a dummy NavController for the preview
        DashboardScreen(navController = rememberNavController())
    }
}