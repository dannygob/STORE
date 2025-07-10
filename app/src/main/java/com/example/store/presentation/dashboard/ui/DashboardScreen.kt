package com.example.store.presentation.dashboard.ui

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
import androidx.compose.material3.Button
import androidx.compose.material.icons.filled.Logout // New Import
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.store.presentation.common.navigation.ScreenRoutes
import com.example.store.presentation.dashboard.DashboardViewModel
import com.example.store.presentation.dashboard.model.NotificationItemUi
import com.example.store.presentation.dashboard.model.NotificationType

// Custom Colors
val Brown = Color(0xFF8D6E63) // A Material Design like brown
val GreenPositive = Color(0xFF4CAF50) // Material Green 500
val RedNegative = Color(0xFFF44336) // Material Red 500


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
    viewModel: DashboardViewModel = viewModel() // Added DashboardViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showNotificationsPanel by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.navigateToLogin.collect {
            navController.navigate(com.example.store.presentation.common.navigation.Route.Login.route) {
                popUpTo(com.example.store.presentation.common.navigation.Route.Dashboard.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Extraer datos a funciones separadas para mejor organización
    val dashboardItems = getDashboardItems()
    // Pass NavController to getMenuItems
    val menuItems = getMenuItems(context, navController)
    val dropdownSections = getDropdownSections()

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
                        "Store Dashboard",
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
                                contentDescription = "Notifications"
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.signOut() }) { // Sign Out Button
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Sign Out"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(), // Behavior needed for shadow with content scroll

                modifier = Modifier.shadow(4.dp) // Added shadow to TopAppBar
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
            Button(
                onClick = { navController.navigate(com.example.store.presentation.common.navigation.Route.Debug.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Go to DB Debug Screen (TEMP)")
            }

            // Menú horizontal mejorado
            HorizontalMenuBar(
                items = menuItems,
                modifier = Modifier.padding(top = 8.dp) // Adjusted padding
            )

            // Sección de tarjetas mejorada
            DashboardCardsSection(
                items = dashboardItems,
                viewModel = viewModel, // Pass viewModel here
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

            // Notifications Panel (DropdownMenu as a panel)
            // This DropdownMenu is part of the Scaffold's content but positioned via Box
            // Alternatively, a custom dialog or a bottom sheet could be used for more complex panels.
            if (showNotificationsPanel) {
                // This Box is a bit of a workaround to use DropdownMenu not directly anchored to an action item
                // but more like a panel. For a true side panel, different components would be used.
                // However, for a list popping up from the top, DropdownMenu can be styled.
                // Consider this an "absolute" positioned dropdown relative to the screen.
                // A better approach for a full panel might be a Dialog or a custom Composable that overlays.
                // For simplicity of this step, we use DropdownMenu, knowing its limitations for this use case.

                // Box to align the DropdownMenu to the top-end (where the bell icon is)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                    DropdownMenu(
                        expanded = true, // Controlled by showNotificationsPanel
                        onDismissRequest = { showNotificationsPanel = false },
                        modifier = Modifier
                            .widthIn(max = 300.dp) // Max width for the panel
                            .padding(top = 8.dp, end = 8.dp) // Align under TopAppBar actions
                    ) {
                        if (uiState.isLoadingNotifications) {
                            DropdownMenuItem(
                                text = { Text("Loading notifications...") },
                                onClick = {} // No action
                            )
                        } else if (uiState.notifications.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No new notifications.") },
                                onClick = {} // No action
                            )
                        } else {
                            Text(
                                "Notifications",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            uiState.notifications.forEach { notification ->
                                NotificationDropdownItem(
                                    notification = notification,
                                    onDismiss = { viewModel.dismissNotification(notification.id) },
                                    onClick = {
                                        viewModel.markAsRead(notification.id)
                                        // Potentially navigate to a relevant screen or show details
                                        Toast.makeText(
                                            context,
                                            "Notification '${notification.title}' clicked.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showNotificationsPanel = false // Close panel on item click
                                    }
                                )
                                HorizontalDivider()
                            }
                            // Actions for all notifications
                            if (uiState.notifications.any { notificationItem -> !notificationItem.isRead }) { // Explicit lambda parameter
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.DoneAll,
                                                contentDescription = "Mark all read",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Mark all as read")
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
                                            contentDescription = "Dismiss all",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Dismiss all")
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
        NotificationType.SYSTEM_ALERT -> Icons.Filled.WarningAmber // Or a more specific system icon
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
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) { // Smaller icon button for dismiss
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
    viewModel: DashboardViewModel, // Added viewModel
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
                        viewModel = viewModel, // Pass viewModel
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
    viewModel: DashboardViewModel, // Added viewModel
    modifier: Modifier = Modifier,
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current // For Toasts
    val uiState by viewModel.uiState.collectAsState() // Collect uiState

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // Increased elevation
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
                // Conditionally display the title. If it's "Low Stock & Expiration", title is omitted.
                if (data.title != "Low Stock & Expiration") {
                    val titleStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.title,
                        style = titleStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f) // Allow title to take space
                    )
                } else {
                    // If title is omitted, add a Spacer to keep the Info icon to the right if it were the only element.
                    // However, for "Low Stock & Expiration", the main Info icon for the card itself will also be removed
                    // in favor of specific icons within its new content structure.
                    // For now, let's ensure the structure doesn't break.
                    // The main IconButton for the card will be handled later for this specific card.
                    Spacer(modifier = Modifier.weight(1f)) // Occupy space if title is not shown
                }

                // The generic Info icon for the card. This will be conditionally rendered or altered
                // for the "Low Stock & Expiration" card later.
                // For now, it remains, but its utility for "Low Stock & Expiration" card changes.
                Box { // This box is for the top-right action icon of the card
                    if (data.title != "Low Stock & Expiration") {
                        IconButton(onClick = { showDropdownMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "More options for ${data.title}",
                                tint = Brown
                            )
                        }
                        // Associated DropdownMenu, also conditional
                        DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = { showDropdownMenu = false }
                        ) {
                            val placeholderTitles = setOf("Upcoming Tasks", "General Reminders", "System Health", "Customer Insights")
                            if (data.title in placeholderTitles || data.details.isEmpty()) {
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
                    // If title IS "Low Stock & Expiration", no IconButton or DropdownMenu is rendered here.
                }
            }
            // Spacer after the header row. Only add if the header was actually rendered.
            if (data.title != "Low Stock & Expiration") {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (data.title == "Low Stock & Expiration") {
                var showLowStockDropdown by remember { mutableStateOf(false) }
                var showExpiringDropdown by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp), // Added some vertical padding
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top // Align items to the top of the row
                ) {
                    // Section 1: Low Stock
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp) // Added padding for spacing between columns
                    ) {
                        // Info Icon and Dropdown
                        Box { // Box to anchor the Low Stock DropdownMenu
                            IconButton(onClick = {
                                Toast.makeText(context, "Low Availability Info icon clicked", Toast.LENGTH_SHORT).show()
                                showLowStockDropdown = true
                            }) {
                                Icon(Icons.Filled.Info, "Low Availability details", tint = Brown, modifier = Modifier.size(30.dp))
                            }
                            DropdownMenu(
                                expanded = showLowStockDropdown,
                                onDismissRequest = { showLowStockDropdown = false },
                                modifier = Modifier.width(220.dp) // Applied fixed width to DropdownMenu
                            ) {
                                if (uiState.lowStockItemsList.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No low availability items.") },
                                        onClick = { showLowStockDropdown = false }
                                    )
                                } else {
                                    Box(modifier = Modifier.height(120.dp)) {
                                        LazyColumn(modifier = Modifier.fillMaxWidth()) { // Added fillMaxWidth
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
                        // Header Text
                        Text("Low Availability", style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Count or OK Icon
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

                    // Section 2: Expiring Soon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp) // Added padding for spacing between columns
                    ) {
                        // Info Icon and Dropdown
                        Box { // Box to anchor the Expiring DropdownMenu
                            IconButton(onClick = {
                                Toast.makeText(context, "Expiring Soon Info icon clicked", Toast.LENGTH_SHORT).show()
                                showExpiringDropdown = true
                            }) {
                                Icon(Icons.Filled.Info, "Expiring items details", tint = Brown, modifier = Modifier.size(30.dp))
                            }
                            DropdownMenu(
                                expanded = showExpiringDropdown,
                                onDismissRequest = { showExpiringDropdown = false },
                                modifier = Modifier.width(220.dp) // Applied fixed width to DropdownMenu
                            ) {
                                if (uiState.expiringItemsList.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No expiring items.") },
                                        onClick = { showExpiringDropdown = false }
                                    )
                                } else {
                                    Box(modifier = Modifier.height(120.dp)) {
                                        LazyColumn(modifier = Modifier.fillMaxWidth()) { // Added fillMaxWidth
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
                        // Header Text
                        Text("Expiring Soon", style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Count or OK Icon
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
                    text = if (data.details.isNotEmpty() || (data.title in setOf("Upcoming Tasks", "General Reminders", "System Health", "Customer Insights")))
                        "Click the icon for details/actions."
                    else
                        "No details available.",
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
                        Toast.makeText(context, "$option clicked", Toast.LENGTH_SHORT).show() // Ensured it's just a toast
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
        Toast.makeText(context, "Inventory clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(ScreenRoutes.INVENTORY) // Temporarily disabled
    },
    MenuItem(Icons.Filled.ShoppingCart, "Purchases") {
        Toast.makeText(context, "Purchases clicked", Toast.LENGTH_SHORT).show()
        // navController.navigate(ScreenRoutes.PURCHASES) // Temporarily disabled
    },
    MenuItem(Icons.Filled.Sell, "Sales") {
        navController.navigate("sales")
    },
    MenuItem(Icons.Filled.LocalShipping, "Orders") {
        Toast.makeText(context, "Orders clicked", Toast.LENGTH_SHORT).show()
        // navController.navigate(ScreenRoutes.ORDERS) // Temporarily disabled
    },
    MenuItem(Icons.Filled.QrCodeScanner, "Scanner") {
        Toast.makeText(context, "Scanner clicked", Toast.LENGTH_SHORT).show()
        // navController.navigate(ScreenRoutes.SCANNER) // Temporarily disabled
    },
    MenuItem(Icons.Filled.Payment, "Expenses") {
        Toast.makeText(context, "Expenses clicked", Toast.LENGTH_SHORT).show()
        // navController.navigate(ScreenRoutes.EXPENSES) // Temporarily disabled
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
        // Explicitly create a DashboardViewModel for the preview
        DashboardScreen(
            navController = rememberNavController(),
            viewModel = DashboardViewModel() // Explicitly pass a new instance
        )
    }
}
