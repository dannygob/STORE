package com.example.Store.presentation.warehouse.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.Store.domain.usecase.inventory.PickListItem
import com.example.Store.presentation.picking.PickListViewModel


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
