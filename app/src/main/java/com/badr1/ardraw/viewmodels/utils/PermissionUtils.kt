package com.badr1.ardraw.viewmodels.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtils {
    val requiredPermissions = mutableListOf<String>()

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
            permission.contains("READ_MEDIA_VISUAL_USER_SELECTED") -> "Selected Photos"
            permission.contains("READ_EXTERNAL_STORAGE") -> "Storage (Read)"
            permission.contains("WRITE_EXTERNAL_STORAGE") -> "Storage (Write)"
            else -> permission.split(".").lastOrNull() ?: permission
        }
    }


    fun wantedPermissions(permissions: List<String>) {
        requiredPermissions.clear()
        for (permission in permissions) {
            when (permission) {
                "camera" -> {
                    requiredPermissions.add(Manifest.permission.CAMERA)
                }

                "media" -> {
                    when {
//                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> { // Android 14+
//                            requiredPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
//                            requiredPermissions.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
//                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                            requiredPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                        }

                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                            requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }

                        else -> {
                            requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                }
            }
        }
    }
}