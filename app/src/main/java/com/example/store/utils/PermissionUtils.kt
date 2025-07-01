package com.example.store.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

object PermissionUtils {

    @Composable
    fun rememberPermissionLauncher(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: (shouldShowRationale: Boolean) -> Unit,
        onPermanentlyDenied: () -> Unit
    ): ManagedActivityResultLauncher<String, Boolean> {
        val context = LocalContext.current
        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    onPermissionGranted()
                } else {
                    // This is a simplification. A real app might need to check
                    // ActivityCompat.shouldShowRequestPermissionRationale more carefully.
                    // However, the contract itself doesn't directly give us this on *every* denial.
                    // The typical flow is: request -> denied -> (optional) show rationale -> request again -> denied (possibly permanent)
                    // For simplicity here, we'll assume any denial after the first could be permanent or need rationale.
                    // A more robust solution involves tracking if rationale *should* be shown.
                    val activity = context.findActivity()
                    if (activity != null && !activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) { // Example permission
                        // If rationale should NOT be shown, it might be permanently denied.
                        // This logic needs to be more dynamic based on the actual permission requested.
                        // This is a placeholder for a more robust permanent denial check.
                        onPermanentlyDenied()
                    } else {
                        onPermissionDenied(true) // Assume rationale should be shown for now
                    }
                }
            }
        )
    }

    @Composable
    fun rememberMultiplePermissionsLauncher(
        onPermissionsResult: (Map<String, Boolean>) -> Unit
    ): ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> {
        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = onPermissionsResult
        )
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Helper to get activity from context, needed for shouldShowRequestPermissionRationale
    // This might need to be improved depending on context type (e.g. ApplicationContext won't work)
    private fun Context.findActivity(): android.app.Activity? {
        var currentContext = this
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is android.app.Activity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }
}

// Example usage (will be refined in later steps when integrating into screens)
/*
@Composable
fun FeatureRequiresPermission(
    permission: String,
    rationaleTitle: String = "Permission Required",
    rationaleMessage: String,
    permanentlyDeniedMessage: String = "Permission permanently denied. Please enable it in app settings.",
    content: @Composable (isPermissionGranted: Boolean, requestPermission: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermanentlyDeniedDialog by remember { mutableStateOf(false) }
    var permissionGrantedState by remember { mutableStateOf(PermissionUtils.isPermissionGranted(context, permission)) }

    val permissionLauncher = PermissionUtils.rememberPermissionLauncher(
        onPermissionGranted = {
            permissionGrantedState = true
            showRationaleDialog = false
            showPermanentlyDeniedDialog = false
        },
        onPermissionDenied = { shouldShowRationale ->
            permissionGrantedState = false
            if (shouldShowRationale) { // This boolean isn't perfectly accurate from the basic launcher result alone
                showRationaleDialog = true
            } else {
                // This could also be initial denial before rationale is needed, or post-rationale denial
                // For simplicity, merging some logic here.
                // A more complex state machine might be needed for perfect rationale flow.
                 showPermanentlyDeniedDialog = true // Simplified: assume permanent if no rationale
            }
        },
        onPermanentlyDenied = { // This custom callback in rememberPermissionLauncher is a bit of a simplification
            permissionGrantedState = false
            showPermanentlyDeniedDialog = true
        }
    )

    content(permissionGrantedState) {
        if (PermissionUtils.isPermissionGranted(context, permission)) {
            permissionGrantedState = true // Ensure state is up-to-date
            // Directly execute action or update UI if permission already granted
        } else {
            // Here, you might check shouldShowRequestPermissionRationale before launching,
            // or launch and then handle rationale/permanent denial in the callback.
            // The provided launcher simplifies this by trying to guess.
            permissionLauncher.launch(permission)
        }
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text(rationaleTitle) },
            text = { Text(rationaleMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    permissionLauncher.launch(permission) // Request again
                }) { Text("Continue") }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showPermanentlyDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermanentlyDeniedDialog = false },
            title = { Text("Permission Denied") },
            text = { Text(permanentlyDeniedMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showPermanentlyDeniedDialog = false
                    // Intent to app settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showPermanentlyDeniedDialog = false }) { Text("Dismiss") }
            }
        )
    }
}
*/
