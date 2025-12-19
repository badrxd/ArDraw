package com.badr1.ardraw.screens.DrawImageType

import android.net.Uri
import android.graphics.Bitmap

sealed class ImageSourceType {
    data class Url(val url: String) : ImageSourceType()
    data class UriSourceType(val uri: Uri) : ImageSourceType()

    data class BitMap(val bitmap: Bitmap) : ImageSourceType()
}
