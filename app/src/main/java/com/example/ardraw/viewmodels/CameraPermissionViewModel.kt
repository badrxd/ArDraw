package com.example.ardraw.viewmodels

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.core.content.ContextCompat

class CameraPermissionViewModel() : ViewModel() {
    private val _hasCameraPermission = mutableStateOf(false)
    val hasCameraPermission: MutableState<Boolean> = _hasCameraPermission
    private val _permissionRequested = mutableStateOf(false)
    val permissionRequested: MutableState<Boolean> = _permissionRequested

    private val _permanentlyDenied = mutableStateOf(false)
    val permanentlyDenied: MutableState<Boolean> = _permanentlyDenied


    fun updatePermissionStatus(granted: Boolean, deniedPermanently: Boolean = false) {
        _hasCameraPermission.value = granted
        _permissionRequested.value = true
        _permanentlyDenied.value = deniedPermanently
    }


    fun CheckCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
//
//    fun updateCameraPermission(value: Boolean) {
//        _hasCameraPermissionBol.value = value
//    }


}