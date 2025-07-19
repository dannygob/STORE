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
fun OrderPickingScreen() {
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
            Text("Order Picking Screen", modifier = Modifier.align(Alignment.Center))
        }
    }
}
