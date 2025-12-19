package com.badr1.ardraw.screens.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtils {
    // Centralized list of permissions
    val requiredPermissions: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(Manifest.permission.CAMERA
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    // Instant check function to prevent UI flickering
    fun allPermissionsGranted(context: Context): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getPermissionDisplayName(permission: String): String {
        return when {
            permission.contains("CAMERA") -> "Camera"
            permission.contains("READ_MEDIA_IMAGES") -> "Photos"
            permission.contains("READ_EXTERNAL_STORAGE") -> "Storage (Read)"
            permission.contains("WRITE_EXTERNAL_STORAGE") -> "Storage (Write)"
            else -> permission.split(".").lastOrNull() ?: permission
        }
    }
}