package com.badr1.ardraw.viewmodels.utils

import com.badr1.ardraw.screens.DrawImageType.ImageSourceType

fun ImageTypeCheker(image: ImageSourceType):Any {
    val image = when (image) {
        is ImageSourceType.Url -> image.url
        is ImageSourceType.UriSourceType -> image.uri
        is ImageSourceType.BitMap -> image.bitmap
        else -> ImageSourceType.Url("")
    }
    return image
}