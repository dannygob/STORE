package com.example.Store.presentation.dashboard.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.Store.R
import com.example.Store.presentation.common.navigation.Route
import com.example.Store.presentation.common.navigation.ScreenRoutes
import com.example.Store.presentation.dashboard.DashboardViewModel
import com.example.Store.presentation.dashboard.model.NotificationItemUi
import com.example.Store.presentation.dashboard.model.NotificationType

// Custom Colors
val Brown = Color(0xFF8D6E63)
val GreenPositive = Color(0xFF4CAF50)
val RedNegative = Color(0xFFF44336)

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
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showNotificationsPanel by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.navigateToLogin.collect {
            navController.navigate(Route.Login.route) {
                popUpTo(Route.Dashboard.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    val dashboardItems = getDashboardItems(context)
    val menuItems = getMenuItems(context, navController)
    val dropdownSections = getDropdownSections(context)

    LaunchedEffect(key1 = uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onUserMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.store_dashboard),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (uiState.unreadNotificationCount > 0) {
                                Badge { Text("${uiState.unreadNotificationCount}") }
                            }
                        }
                    ) {
                        IconButton(onClick = { showNotificationsPanel = !showNotificationsPanel }) {
                            Icon(
                                imageVector = if (uiState.unreadNotificationCount > 0) Icons.Filled.Notifications else Icons.Outlined.NotificationsNone,
                                contentDescription = stringResource(id = R.string.notifications)
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.signOut() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(id = R.string.sign_out)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                // Move version to the right
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.version),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Improved horizontal menu

            HorizontalMenuBar(
                items = menuItems,
                modifier = Modifier.padding(vertical = 8.dp)
            )


            // Improved card section

            DashboardCardsSection(
                items = dashboardItems,
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )

            ChartsSectionPlaceholder(
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Improved dropdowns section

            DropdownMenusSection(
                sections = dropdownSections,
                modifier = Modifier.padding(16.dp),
                navController = navController
            )

            if (showNotificationsPanel) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { showNotificationsPanel = false },
                        modifier = Modifier
                            .widthIn(max = 300.dp)
                            .padding(top = 8.dp, end = 8.dp)
                    ) {
                        if (uiState.isLoadingNotifications) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.loading_notifications)) },
                                onClick = {}
                            )
                        } else if (uiState.notifications.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.no_new_notifications)) },
                                onClick = {}
                            )
                        } else {
                            Text(
                                stringResource(id = R.string.notifications),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            uiState.notifications.forEach { notification ->
                                NotificationDropdownItem(
                                    notification = notification,
                                    onDismiss = { viewModel.dismissNotification(notification.id) },
                                    onClick = {
                                        viewModel.markAsRead(notification.id)
                                        Toast.makeText(
                                            context,
                                            "Notification '${notification.title}' clicked.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showNotificationsPanel = false
                                    }
                                )
                                HorizontalDivider()
                            }
                            if (uiState.notifications.any { !it.isRead }) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.DoneAll,
                                                contentDescription = stringResource(id = R.string.mark_all_as_read),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(stringResource(id = R.string.mark_all_as_read))
                                        }
                                    },
                                    onClick = { viewModel.markAllAsRead() }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.DeleteSweep,
                                            contentDescription = stringResource(id = R.string.dismiss_all),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(id = R.string.dismiss_all))
                                    }
                                },
                                onClick = { viewModel.dismissAllNotifications() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationDropdownItem(
    notification: NotificationItemUi,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    val iconVector = when (notification.type) {
        NotificationType.ORDER_NEW, NotificationType.ORDER_DELIVERED -> Icons.Filled.ShoppingCart
        NotificationType.LOW_STOCK, NotificationType.ITEM_EXPIRED -> Icons.Filled.WarningAmber
        NotificationType.INFO -> Icons.Filled.Info
        NotificationType.SYSTEM_ALERT -> Icons.Filled.WarningAmber
    }
    val itemWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold

    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = notification.type.name,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    tint = if (notification.type == NotificationType.LOW_STOCK || notification.type == NotificationType.ITEM_EXPIRED || notification.type == NotificationType.SYSTEM_ALERT) MaterialTheme.colorScheme.error else LocalContentColor.current
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(notification.title, fontWeight = itemWeight, style = MaterialTheme.typography.bodyMedium)
                    Text(notification.message, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    Text(notification.getFormattedTimestamp(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Dismiss notification", modifier = Modifier.size(18.dp))
                }
            }
        },
        onClick = onClick
    )
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
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            color = Color.Transparent
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp), // Slightly larger icon
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium, // Larger font
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun DashboardCardsSection(
    items: List<DashboardData>,
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Increased spacing
    ) {
        items(items.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Increased spacing
            ) {
                rowItems.forEach { item ->
                    DashboardCard(
                        data = item,
                        viewModel = viewModel,
                        modifier = Modifier.weight(1f)
                    )
                }
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
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    Card(
        modifier = modifier.clickable {
            Toast.makeText(context, "${data.title} card clicked", Toast.LENGTH_SHORT).show()
        },
        shape = RoundedCornerShape(16.dp), // More rounded corners
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (data.title != "Low Stock & Expiration") {
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Box {
                    if (data.title != "Low Stock & Expiration") {
                        IconButton(onClick = { showDropdownMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "More options for ${data.title}",
                                tint = Brown
                            )
                        }
                        DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = { showDropdownMenu = false }
                        ) {
                            data.details.forEach { detail ->
                                DropdownMenuItem(
                                    text = { Text(detail) },
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "Selected: $detail",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showDropdownMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            if (data.title != "Low Stock & Expiration") {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (data.title == "Low Stock & Expiration") {
                var showLowStockDropdown by remember { mutableStateOf(false) }
                var showExpiringDropdown by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    ) {
                        Box {
                            IconButton(onClick = {
                                showLowStockDropdown = true
                            }) {
                                Icon(Icons.Filled.Info, "Low Availability details", tint = Brown, modifier = Modifier.size(30.dp))
                            }
                            DropdownMenu(
                                expanded = showLowStockDropdown,
                                onDismissRequest = { showLowStockDropdown = false },
                                modifier = Modifier.width(220.dp)
                            ) {
                                if (uiState.lowStockItemsList.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No low availability items.") },
                                        onClick = { showLowStockDropdown = false }
                                    )
                                } else {
                                    Box(modifier = Modifier.height(120.dp)) {
                                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                            items(uiState.lowStockItemsList, key = { it.id }) { item ->
                                                DropdownMenuItem(
                                                    text = { Text(item.message) },
                                                    onClick = {
                                                        Toast.makeText(context, item.message, Toast.LENGTH_SHORT).show()
                                                        showLowStockDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Low Stock",
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.lowStockItemCount > 0) {
                            Text(
                                text = "${uiState.lowStockItemCount}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = RedNegative
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Low stock OK",
                                tint = GreenPositive,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    ) {
                        Box {
                            IconButton(onClick = {
                                showExpiringDropdown = true
                            }) {
                                Icon(Icons.Filled.Info, "Expiring items details", tint = Brown, modifier = Modifier.size(30.dp))
                            }
                            DropdownMenu(
                                expanded = showExpiringDropdown,
                                onDismissRequest = { showExpiringDropdown = false },
                                modifier = Modifier.width(220.dp)
                            ) {
                                if (uiState.expiringItemsList.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No expiring items.") },
                                        onClick = { showExpiringDropdown = false }
                                    )
                                } else {
                                    Box(modifier = Modifier.height(120.dp)) {
                                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                            items(uiState.expiringItemsList, key = { it.id }) { item ->
                                                DropdownMenuItem(
                                                    text = { Text(item.message) },
                                                    onClick = {
                                                        Toast.makeText(context, item.message, Toast.LENGTH_SHORT).show()
                                                        showExpiringDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Exp Soon",
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.expiringItemCount > 0) {
                            Text(
                                text = "${uiState.expiringItemCount}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = RedNegative
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Expiring items OK",
                                tint = GreenPositive,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = if (data.details.isNotEmpty()) "Click for details" else "No details available.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun DropdownMenusSection(
    sections: List<DropdownSection>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        sections.forEach { section ->
            DropdownMenuComponent(
                icon = section.icon,
                label = section.label,
                options = section.options,
                navController = navController
            )
        }
    }
}

@Composable
private fun DropdownMenuComponent(
    icon: ImageVector,
    label: String,
    options: List<String>,
    navController: NavController
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
                    text = { Text(option) },
                    onClick = {
                        when (option) {
                            "View Orders" -> navController.navigate(Route.Warehouse.route)
                            "Manage Stock" -> navController.navigate(Route.Inventory.route)
                            else -> Toast.makeText(context, "$option clicked", Toast.LENGTH_SHORT).show()
                        }
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun getDashboardItems(context: Context) = listOf(
    DashboardData(context.getString(R.string.order_alerts), listOf("Order #101 - Pending", "Order #102 - Payment Due")),
    DashboardData(context.getString(R.string.low_stock_expiration), listOf()),
    DashboardData(
        context.getString(R.string.sales_statistics),
        listOf("Today's Sales: $1,234.56", "Top Product: Super Widget")
    ),
    DashboardData(context.getString(R.string.admin_balance), listOf("Main Account: $15,000.00", "Pending: $1,200.00")),
    DashboardData(context.getString(R.string.expenses_services), listOf("Rent: Due in 5 days", "Electricity: Paid")),
    DashboardData(context.getString(R.string.other_expenses), listOf("Maintenance: $300", "Supplies: $150")),
    DashboardData(context.getString(R.string.customer_insights), listOf("New Customers: 5", "Top Spender: J. Doe")),
    DashboardData(context.getString(R.string.system_health), listOf("Backup: OK", "API Latency: 120ms")),
    DashboardData(context.getString(R.string.upcoming_tasks), listOf("Call Supplier X", "Staff Meeting @ 3pm")),
    DashboardData(context.getString(R.string.general_reminders), listOf("Bank Holiday Monday", "Submit Tax Forms"))
)

private fun getMenuItems(context: Context, navController: NavController) = listOf(
    MenuItem(Icons.Filled.Inventory, context.getString(R.string.inventory)) {
        Toast.makeText(context, "Inventory clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(ScreenRoutes.INVENTORY) {}
    },
    MenuItem(Icons.Filled.ShoppingCart, context.getString(R.string.purchases)) {
        Toast.makeText(context, "Purchases clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(ScreenRoutes.PURCHASES) {}
    },
    MenuItem(Icons.Filled.Sell, context.getString(R.string.sales)) {
        Toast.makeText(context, "Sales clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(ScreenRoutes.SALES) {}
    },
    MenuItem(Icons.Filled.LocalShipping, context.getString(R.string.orders)) {
        Toast.makeText(context, "Orders clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(ScreenRoutes.ORDERS) {}
    },
    MenuItem(Icons.Filled.QrCodeScanner, context.getString(R.string.scanner)) {
        Toast.makeText(context, "Scanner clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(ScreenRoutes.SCANNER) {}
    },
    MenuItem(Icons.Filled.Payment, context.getString(R.string.expenses)) {
        Toast.makeText(context, "Expenses clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(ScreenRoutes.EXPENSES) {}
    },
    MenuItem(Icons.Filled.LocalShipping, context.getString(R.string.warehouse)) {
        Toast.makeText(context, "Warehouse clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(Route.Warehouse.route) {}
    }
)

private fun getDropdownSections(context: Context) = listOf(
    DropdownSection(
        Icons.Filled.LocalShipping,
        context.getString(R.string.orders),
        listOf(
            context.getString(R.string.create_order),
            context.getString(R.string.order_status),
            context.getString(R.string.shipping_notification)
        )
    ),
    DropdownSection(
        Icons.Filled.Add,
        context.getString(R.string.products),
        listOf(
            context.getString(R.string.add_product),
            context.getString(R.string.product_return)
        )
    ),
    DropdownSection(
        Icons.Filled.Person,
        context.getString(R.string.people),
        listOf(
            context.getString(R.string.add_customer),
            context.getString(R.string.add_supplier)
        )
    ),
    DropdownSection(
        Icons.Filled.LocalShipping,
        context.getString(R.string.warehouse),
        listOf(
            context.getString(R.string.view_orders),
            context.getString(R.string.manage_stock)
        )
    )
)

@Composable
private fun ChartsSectionPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.charts_area_coming_soon),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DashboardPreview() {
    LocalContext.current
    val fakeAuthRepository = object : com.example.Store.domain.repository.AuthRepository {
        override suspend fun login(
            email: String,
            password: String,
        ): Result<com.example.Store.domain.model.LoginResult> {
            return Result.success(com.example.Store.domain.model.LoginResult(com.example.Store.domain.model.UserRole.ADMIN))
        }

        override suspend fun register(
            email: String,
            password: String,
            role: com.example.Store.domain.model.UserRole,
        ): Result<Unit> {
            return Result.success(Unit)
        }
        override suspend fun recoverPassword(email: String): Result<Unit> {
            return Result.success(Unit)
        }
        override suspend fun signOut(): Result<Unit> {
            return Result.success(Unit)
        }
        override fun getAuthState(): kotlinx.coroutines.flow.Flow<com.google.firebase.auth.FirebaseUser?> {
            return kotlinx.coroutines.flow.flowOf(null)
        }
    }
    val fakeViewModel = DashboardViewModel(fakeAuthRepository)

    MaterialTheme {
        DashboardScreen(
            navController = rememberNavController(),
            viewModel = fakeViewModel
        )
    }
}
