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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    viewModel: LocationListViewModel = hiltViewModel(),
    onAddLocationClick: () -> Unit,
    onLocationClick: (String) -> Unit
) {
    val locations by viewModel.locations.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Warehouse Locations") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                Toast.makeText(context, "Add Location clicked", Toast.LENGTH_SHORT).show()
                onAddLocationClick()
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Location")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(locations) { location ->
                ListItem(
                    headlineText = { Text(location.name) },
                    supportingText = { Text(location.address ?: "No address") },
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Location ${location.locationId} clicked", Toast.LENGTH_SHORT).show()
                        onLocationClick(location.locationId)
                    }
                )
            }
        }
    }
}