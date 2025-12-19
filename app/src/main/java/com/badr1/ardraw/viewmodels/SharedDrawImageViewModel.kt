package com.badr1.ardraw.viewmodels
import androidx.lifecycle.ViewModel
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class  SharedDrawImageViewModel: ViewModel() {

    private val _selectedImage = MutableStateFlow<ImageSourceType?>(null)
    val selectedImage: StateFlow<ImageSourceType?> = _selectedImage

    fun setSelectedImage(source: ImageSourceType) {
        _selectedImage.value = source
    }
}
