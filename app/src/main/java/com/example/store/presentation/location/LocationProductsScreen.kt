package com.example.store.presentation.location


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationProductsScreen(
    viewModel: LocationProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.location?.name ?: "Location Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            if (uiState.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            } else {
                items(uiState.products) { productLocation ->
                    ListItem(
                        headlineContent = { Text("Product ID: ${productLocation.productId}") },
                        supportingContent = {
                            Column {
                                Text("Quantity: ${productLocation.quantity}")
                                val aisle = productLocation.aisle ?: "N/A"
                                val shelf = productLocation.shelf ?: "N/A"
                                val level = productLocation.level ?: "N/A"
                                Text("Location: Aisle $aisle, Shelf $shelf, Level $level")
                            }
                        }
                    )
                }
            }
        }
    }
}
