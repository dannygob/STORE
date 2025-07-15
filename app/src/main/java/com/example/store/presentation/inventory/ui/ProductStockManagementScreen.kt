package com.example.store.presentation.inventory.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.store.presentation.inventory.ProductStockManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductStockManagementScreen(
    viewModel: ProductStockManagementViewModel = hiltViewModel(),
    onSaveFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var locationId by remember { mutableStateOf("") }
    var aisle by remember { mutableStateOf("") }
    var shelf by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSaveFinished()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manage Stock for Product ${viewModel.productId}") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Assign to New Location", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(value = locationId, onValueChange = { locationId = it }, label = { Text("Location ID") })
            OutlinedTextField(value = aisle, onValueChange = { aisle = it }, label = { Text("Aisle") })
            OutlinedTextField(value = shelf, onValueChange = { shelf = it }, label = { Text("Shelf") })
            OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("Level") })
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })

            Button(
                onClick = {
                    viewModel.assignToNewLocation(
                        locationId = locationId,
                        aisle = aisle.takeIf { it.isNotBlank() },
                        shelf = shelf.takeIf { it.isNotBlank() },
                        level = level.takeIf { it.isNotBlank() },
                        amount = amount.toIntOrNull() ?: 0
                    )
                },
                enabled = !uiState.isLoading
            ) {
                Text("Assign Stock")
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            uiState.error?.let {
                Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
