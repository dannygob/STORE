package com.example.Store.presentation.location


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    viewModel: LocationListViewModel = hiltViewModel(),
    onAddLocationClick: () -> Unit,
    onLocationClick: (String) -> Unit
) {
    val locations by viewModel.locations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Warehouse Locations") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLocationClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Location")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(locations) { location ->
                ListItem(
                    headlineText = { Text(location.name) },
                    supportingText = { Text(location.address ?: "No address") },
                    modifier = Modifier.clickable { onLocationClick(location.locationId) },
                    headlineContent = TODO(),
                    overlineContent = TODO(),
                    supportingContent = TODO(),
                    leadingContent = TODO(),
                    trailingContent = TODO(),
                    colors = TODO(),
                    tonalElevation = TODO(),
                    shadowElevation = TODO()
                )
            }
        }
    }
}