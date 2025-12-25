package com.badr1.ardraw.viewmodels

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.badr1.ardraw.SUB_FOLDER_NAME
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedImage = MutableStateFlow<ImageSourceType?>(null)
    val selectedImage: StateFlow<ImageSourceType?> = _selectedImage

    fun loadImagesFromFolder() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val imageList = mutableListOf<Uri>()
            val selection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.RELATIVE_PATH + " LIKE ? "
                } else {
                    MediaStore.Images.Media.DATA + " LIKE ?"
                }

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED
            )
            val selectionArgs = arrayOf("%$SUB_FOLDER_NAME%")
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            try {
                getApplication<Application>().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        imageList.add(contentUri)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _images.value = imageList
            _isLoading.value = false
        }
    }

    private val _deleteIntentSender = MutableStateFlow<IntentSender?>(null)
    val deleteIntentSender: StateFlow<IntentSender?> = _deleteIntentSender.asStateFlow()

    fun deleteImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resolver = getApplication<Application>().contentResolver

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // ✅ Android 11+ requires user confirmation via IntentSender
                    val intentSender = MediaStore.createDeleteRequest(
                        resolver,
                        listOf(uri)
                    ).intentSender

                    // Send the IntentSender to the UI layer
                    _deleteIntentSender.value = intentSender

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // ✅ Android 10 - try direct delete, catch SecurityException
                    try {
                        val rowsDeleted = resolver.delete(uri, null, null)
                        if (rowsDeleted > 0) {
                            _images.value = _images.value.filter { it != uri }
                        }
                    } catch (e: RecoverableSecurityException) {
                        // Need user permission
                        _deleteIntentSender.value = e.userAction.actionIntent.intentSender
                    }
                } else {
                    // ✅ Android 9 and below - direct delete works
                    val rowsDeleted = resolver.delete(uri, null, null)
                    if (rowsDeleted > 0) {
                        _images.value = _images.value.filter { it != uri }
                    }
                }
            } catch (e: Exception) {
                Log.e("GalleryViewModel", "Error deleting image", e)
                e.printStackTrace()
            }
        }
    }

    // Call this after user confirms deletion
    fun onDeleteConfirmed(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _images.value = _images.value.filter { it != uri }
            _deleteIntentSender.value = null // Reset
        }
    }

    fun clearDeleteRequest() {
        _deleteIntentSender.value = null
    }

    fun selectImage(image: ImageSourceType) {
        _selectedImage.value = image
    }
}