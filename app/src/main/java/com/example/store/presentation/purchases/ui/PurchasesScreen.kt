package com.example.store.presentation.purchases.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Correct import
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview // Added for Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Added for Preview
import com.example.store.presentation.purchases.PurchasesViewModel
import com.example.store.presentation.purchases.model.PurchaseItemUi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(
    navController: NavController,
    viewModel: PurchasesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onUserMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Purchase History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.recordNewPurchasePlaceholder()
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Record New Purchase")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.purchases.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No purchase history found.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.purchases, key = { it.id }) { purchase ->
                        PurchaseListItem(
                            purchase = purchase,
                            onClick = { viewModel.viewPurchaseDetailsPlaceholder(purchase.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PurchasesScreenPreview() {
    MaterialTheme {
        PurchasesScreen(
            navController = rememberNavController(),
            viewModel = PurchasesViewModel() // Real VM for preview
        )
    }
}

@Composable
fun PurchaseListItem(
    purchase: PurchaseItemUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                purchase.productName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Qty: ${purchase.quantity} @ ${purchase.getFormattedUnitPrice()} each", style = MaterialTheme.typography.bodyMedium)
            purchase.supplierName?.let {
                Text("Supplier: $it", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total: ${purchase.getFormattedTotalPrice()}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    purchase.getFormattedDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
