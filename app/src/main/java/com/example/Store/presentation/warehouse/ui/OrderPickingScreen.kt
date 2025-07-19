package com.example.store.presentation.picking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.Store.domain.usecase.inventory.PickListItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderPickingScreen(
    viewModel: PickListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Order Pick List") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.pickList) { item ->
                        PickListItemCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun PickListItemCard(item: PickListItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Product: ${item.productName}", style = MaterialTheme.typography.titleMedium)
            Text("Quantity to Pick: ${item.quantityToPick}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Locations:", style = MaterialTheme.typography.titleSmall)
            if (item.availableLocations.isEmpty()) {
                Text("No stock available for this product.")
            } else {
                item.availableLocations.forEach { location ->
                    Text("- Qty ${location.quantity} at Aisle: ${location.aisle ?: "N/A"}, Shelf: ${location.shelf ?: "N/A"}, Level: ${location.level ?: "N/A"}")
                }
            }
        }
    }
}
