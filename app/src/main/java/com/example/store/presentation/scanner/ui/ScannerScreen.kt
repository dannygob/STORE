package com.example.store.presentation.scanner.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border // Explicit import for Modifier.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Correct
import androidx.compose.material.icons.filled.CameraAlt
// import androidx.compose.material.icons.filled.History // Not used directly in this version
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview // Added
import androidx.compose.ui.unit.dp
// import androidx.compose.ui.unit.sp // Not used directly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Added
import com.example.store.presentation.scanner.ScannerViewModel
import com.example.store.presentation.scanner.model.ScannedDataUi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    navController: NavController,
    viewModel: ScannerViewModel = viewModel()
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
                title = { Text("Barcode Scanner") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Placeholder for Camera Preview / Scanning Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isScanningActive) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Scanning...", style = MaterialTheme.typography.titleMedium)
                    }
                } else {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = "Scanner Area",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (uiState.isScanningActive) viewModel.cancelScan() else viewModel.startScan()
                    }
                ) {
                    Text(if (uiState.isScanningActive) "Cancel Scan" else "Start Scan")
                }
                // Simulate a scan for testing UI without actual camera
                Button(onClick = { viewModel.processScannedCode("TEST-CODE-${(100..999).random()}") }) {
                    Text("Simulate Scan")
                }
            }

            // Last Scanned Info
            Text(
                text = "Last Scanned:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            uiState.lastScannedItem?.let {
                ScannedDataItemView(item = it)
                Button(
                    onClick = { viewModel.clearLastScan() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Clear Last Scan")
                }
            } ?: Text("No code scanned yet.", style = MaterialTheme.typography.bodyLarge)


            // Scan History (Optional - basic version)
            if (uiState.scanHistory.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Scan History (${uiState.scanHistory.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { viewModel.clearScanHistory() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Clear Scan History", tint = MaterialTheme.colorScheme.error)
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Takes remaining space
                ) {
                    items(uiState.scanHistory.reversed()) { item -> // Show newest first
                        ScannedDataItemView(item = item, isCompact = true)
                        Divider()
                    }
                }
            } else {
                 Text("Scan history is empty.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScannerScreenPreview() {
    MaterialTheme {
        ScannerScreen(
            navController = rememberNavController(),
            viewModel = ScannerViewModel() // Real VM for preview
        )
    }
}

@Composable
fun ScannedDataItemView(item: ScannedDataUi, isCompact: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isCompact) 4.dp else 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompact) 1.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(if (isCompact) 8.dp else 16.dp)) {
            Text(
                text = item.content,
                style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(if (isCompact) 2.dp else 4.dp))
            Text(
                text = "Scanned: ${item.getFormattedTimestamp()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
