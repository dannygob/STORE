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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.presentation.inventory.InventoryViewModel
import com.example.store.presentation.inventory.model.InventoryItemUi
import com.example.store.presentation.inventory.model.InventoryTab
import com.example.store.presentation.inventory.model.InventoryUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.selectedProduct == null) "Inventory" else state.selectedProduct!!.name) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.selectedProduct != null) {
                            viewModel.onDismissProductDetail()
                        } else {
                            navController.navigateUp()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (state.selectedProduct == null) {
            InventoryListContent(
                state = state,
                modifier = Modifier.padding(padding),
                onSearchChanged = viewModel::onSearchChanged,
                onTabSelected = viewModel::onTabSelected,
                onProductClick = viewModel::onProductSelected
            )
        } else {
            ProductDetailView(
                state = state,
                modifier = Modifier.padding(padding),
                onManageStockClick = { productId ->
                    navController.navigate("${com.example.store.presentation.common.navigation.Route.ProductStockManagement.route}/$productId")
                }
            )
        }
    }
}

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
fun InventoryListContent(
    state: InventoryUiState,
    modifier: Modifier = Modifier,
    onSearchChanged: (String) -> Unit,
    onTabSelected: (InventoryTab) -> Unit,
    onProductClick: (String) -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = state.searchText,
            onValueChange = onSearchChanged,
            label = { Text("Search Products") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        // Tabs can be re-enabled if filtering logic is added back
        // TabRow(...)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.filteredItems) { item ->
                InventoryCard(item, onClick = { onProductClick(item.id) })
            }
        }
    }
}

@Composable
fun ProductDetailView(
    state: InventoryUiState,
    modifier: Modifier = Modifier,
    onManageStockClick: (String) -> Unit
) {
    val product = state.selectedProduct
    if (product == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found.")
        }
        return
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Name: ${product.name}", style = MaterialTheme.typography.headlineSmall)
        Text("Description: ${product.description ?: "N/A"}")
        Text("Stock: ${product.stock}")
        Text("Price: ${product.salePrice}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onManageStockClick(product.id) }) {
            Text("Manage Stock")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Locations", style = MaterialTheme.typography.titleMedium)

        if (state.productLocations.isEmpty()) {
            Text("This product is not currently stored in any location.")
        } else {
            LazyColumn {
                items(state.productLocations) { location ->
                    ProductLocationCard(location)
                }
            }
        }
    }
}

@Composable
fun ProductLocationCard(location: ProductLocationEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Location ID: ${location.locationId}") // In a real app, you'd look up the location name
            Text("Quantity: ${location.quantity}")
            Text("Aisle: ${location.aisle ?: "N/A"}")
            Text("Shelf: ${location.shelf ?: "N/A"}")
            Text("Level: ${location.level ?: "N/A"}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryCard(item: InventoryItemUi, onClick: () -> Unit) {
    val stockColor = when {
        item.isOutOfStock() -> Color.Red
        item.isLowStock() -> Color(0xFFFFA500)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
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
                    Text("⚠️ Expiring soon", color = Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    InventoryScreen(
        navController = rememberNavController()
    )
}
