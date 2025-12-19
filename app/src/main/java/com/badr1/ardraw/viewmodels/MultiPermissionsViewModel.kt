package com.badr1.ardraw.viewmodels

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

enum class PermissionStatus {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

data class PermissionResult(
    val status: PermissionStatus = PermissionStatus.DENIED
)

class MultiPermissionsViewModel : ViewModel() {

    private val _permissions = mutableStateMapOf<String, PermissionResult>()
    val permissions: Map<String, PermissionResult> get() = _permissions

    fun updatePermission(permission: String, status: PermissionStatus) {
        _permissions[permission] = PermissionResult(status)
    }

    fun resetPermission(permission: String) {
        _permissions[permission] = PermissionResult(PermissionStatus.DENIED)
    }
}
