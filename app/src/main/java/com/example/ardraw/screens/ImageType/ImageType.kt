package com.example.ardraw.screens.ImageType

import android.net.Uri

sealed class ImageSource {
    data class Url(val url: String) : ImageSource()
    data class UriSource(val uri: Uri) : ImageSource()
}
