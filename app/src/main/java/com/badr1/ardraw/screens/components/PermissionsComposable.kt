package com.badr1.ardraw.screens.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.badr1.ardraw.viewmodels.utils.PermissionUtils
import com.badr1.ardraw.viewmodels.MultiPermissionsViewModel
import com.badr1.ardraw.viewmodels.PermissionStatus

@Composable
fun RequestPermissions(
    viewModel: MultiPermissionsViewModel = viewModel(),
    onAllGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    permissions: List<String>
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var permissionsRequested by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        var softDenied = false // Flag for non-permanent denial
        var permanentlyDenied = false // Flag for permanent denial

        map.forEach { (perm, granted) ->
            val isPermanent = !granted && activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(it, perm)
            } ?: false

            val status = if (granted) {
                PermissionStatus.GRANTED
            } else if (isPermanent) {
                permanentlyDenied = true
                PermissionStatus.PERMANENTLY_DENIED
            } else {
                softDenied = true
                PermissionStatus.DENIED
            }
            viewModel.updatePermission(perm, status)
        }

        permissionsRequested = true

        // If it was a soft denial (not permanent, and not all granted),
        // we trigger the parent to hide this composable.
        if (softDenied && !permanentlyDenied) {
            onPermissionDenied() // <-- Call the new handler
        }
    }

    val currentStates = PermissionUtils.requiredPermissions.associateWith { perm ->
        if (PermissionUtils.allPermissionsGranted(context)) PermissionStatus.GRANTED
        else viewModel.permissions[perm]?.status ?: PermissionStatus.DENIED
    }

//    val currentStates = PermissionUtils.requiredPermissions.associateWith { perm ->
//        if (PermissionUtils.allPermissionsGranted(context)) PermissionStatus.GRANTED
//        else viewModel.permissions[perm]?.status ?: PermissionStatus.DENIED
//    }

    val hasPermanentlyDenied =
        currentStates.values.any { it == PermissionStatus.PERMANENTLY_DENIED }

    // Logic to trigger request or callback
    LaunchedEffect(permissionsRequested) {
        if (PermissionUtils.requiredPermissions.isEmpty()) {
            PermissionUtils.wantedPermissions(permissions)
        }

        if (PermissionUtils.allPermissionsGranted(context)) {
            onAllGranted()
        } else if (!permissionsRequested) {
            launcher.launch(PermissionUtils.requiredPermissions.toTypedArray())
        }
    }

    if (hasPermanentlyDenied && permissionsRequested) {
        PermissionDeniedDialog(
            deniedPermissions = currentStates.filter { it.value == PermissionStatus.PERMANENTLY_DENIED }.keys.toList(),
            onOpenSettings = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            },
            onRetry = { permissionsRequested = false }
        )
    }
}

@Composable
private fun PermissionDeniedDialog(
    deniedPermissions: List<String>,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Permissions Required") },
        text = {
            androidx.compose.foundation.layout.Column {
                Text("Please enable these permissions in settings:")
                deniedPermissions.forEach {
                    val permission = PermissionUtils.getPermissionDisplayName(it)
                    if (permission == "Photos") Text(
                        "• ${permission}: allow all",
                        color = MaterialTheme.colors.error
                    ) else Text(
                        "• $permission",
                        color = MaterialTheme.colors.error
                    )
                }
            }
        },
        confirmButton = { Button(onClick = onOpenSettings) { Text("Settings") } },
        dismissButton = { TextButton(onClick = onRetry) { Text("Retry") } }
    )
}