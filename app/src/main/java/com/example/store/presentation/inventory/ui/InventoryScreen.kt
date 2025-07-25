package com.example.store.presentation.inventory.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.store.presentation.inventory.InventoryViewModel


// MOCK DE DATOS Y FUNCIONALIDAD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    navController: NavController,
    state: InventoryUiState,
    onSearchChanged: (String) -> Unit,
    onTabSelected: (InventoryTab) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        InventoryScreenContent(
            state = state,
            modifier = Modifier.padding(padding),
            onSearchChanged = onSearchChanged,
            onTabSelected = onTabSelected
        )
    }
}

enum class InventoryTab { ALL, LOW_STOCK }

data class InventoryItemUi(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val category: String,
) {
    fun isOutOfStock() = quantity == 0
    fun isLowStock() = quantity in 1..3
    fun isExpiringSoon() = name.contains("Leche", ignoreCase = true)
    fun getFormattedPrice(): String = "$${"%.2f".format(price)}"
}

data class InventoryUiState(
    val items: List<InventoryItemUi> = emptyList(),
    val filteredItems: List<InventoryItemUi> = emptyList(),
    val searchText: String = "",
    val selectedTab: InventoryTab = InventoryTab.ALL,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
)

@Composable
fun InventoryScreenContent(
    state: InventoryUiState,
    modifier: Modifier = Modifier,
    onSearchChanged: (String) -> Unit = {},
    onTabSelected: (InventoryTab) -> Unit = {},
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = state.searchText,
            onValueChange = onSearchChanged,
            label = { Text("Search product") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = state.selectedTab.ordinal) {
            InventoryTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = index == state.selectedTab.ordinal,
                    onClick = { onTabSelected(tab) },
                    text = { Text(tab.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SummaryRow(state)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.filteredItems) { item ->
                InventoryCard(item)
            }
        }
    }
}

@Composable
fun SummaryRow(state: InventoryUiState) {
    val total = state.items.size
    val stock = state.items.sumOf { it.quantity }
    val value = state.items.sumOf { it.price * it.quantity }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total: $total")
            Text("Stock: $stock")
            Text("Value: $${"%.2f".format(value)}")
        }
    }
}

@Composable
fun InventoryCard(item: InventoryItemUi) {
    val stockColor = when {
        item.isOutOfStock() -> Color.Red
        item.isLowStock() -> Color(0xFFFFA500)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(android.R.drawable.ic_menu_gallery),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("Quantity: ${item.quantity}", color = stockColor)
                Text("Price: ${item.getFormattedPrice()}")
                Text("Category: ${item.category}")
                if (item.isExpiringSoon()) {
                    Text("⚠️ Expires soon", color = Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    val items = listOf(
        InventoryItemUi("1", "Leche Entera", 5, 1.25, "Lácteos"),
        InventoryItemUi("2", "Arroz", 0, 0.85, "Granos"),
        InventoryItemUi("3", "Huevos", 2, 0.25, "Proteínas")
    )
    InventoryScreen(
        navController = rememberNavController(),
        state = InventoryUiState(
            items = items,
            filteredItems = items
        ),
        onSearchChanged = {},
        onTabSelected = {}
    )
}
