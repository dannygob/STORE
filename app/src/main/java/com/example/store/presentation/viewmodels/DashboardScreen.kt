import android.widget.Toast
import androidx.compose.foundation.background
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

data class DashboardData(val title: String, val details: List<String>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val dashboardItems = listOf(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store Dashboard") },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        },
        bottomBar = {
            BottomAppBar {
                Text(
                    text = "Version 1.0",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🔷 Horizontal Menu with elevation
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Icons.Filled.Inventory to "Inventory",
                        Icons.Filled.ShoppingCart to "Purchases",
                        Icons.Filled.Sell to "Sales",
                        Icons.Filled.LocalShipping to "Orders",
                        Icons.Filled.QrCodeScanner to "Scanner",
                        Icons.Filled.Payment to "Expenses"
                    ).forEach { (icon, label) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp)
                        ) {
                            Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
                            Text(label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // 🔷 Cards Section
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(dashboardItems.chunked(2)) { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    item.details.forEach { detail ->
                                        Text(
                                            text = "- $detail",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // 🔷 Bottom Dropdown Menus
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DropdownMenuSection(
                    icon = Icons.Filled.LocalShipping,
                    label = "Orders",
                    options = listOf(
                        "Create Order",
                        "Order Status",
                        "Order Cancellation",
                        "Shipping Notification"
                    )
                )
                DropdownMenuSection(
                    icon = Icons.Filled.Add,
                    label = "Products",
                    options = listOf(
                        "Add Product",
                        "Product Return"
                    )
                )
                DropdownMenuSection(
                    icon = Icons.Filled.Person,
                    label = "People",
                    options = listOf(
                        "Add Customer",
                        "Add Supplier",
                        "Add Service Provider"
                    )
                )
            }
        }
    }
}

@Composable
fun DropdownMenuSection(
    icon: ImageVector,
    label: String,
    options: List<String>,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label)
            Text(label, modifier = Modifier.padding(start = 4.dp))
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Expand $label")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        Toast.makeText(context, option, Toast.LENGTH_SHORT).show()
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DashboardPreview() {
    MaterialTheme {
        DashboardScreen()
    }
}
