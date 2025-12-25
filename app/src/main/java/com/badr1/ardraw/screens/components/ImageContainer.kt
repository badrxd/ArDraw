package com.badr1.ardraw.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.badr1.ardraw.R

import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.viewmodels.utils.ImageTypeCheker

private val PLACEHOLDER_ID = R.drawable.rounded_imagesmode_24
private val ERROR_ID = R.drawable.outline_error_24

@Composable
fun LargeImageBox(image: ImageSourceType, minHeight: Int, maxHeight:Int) {

    val imageToShow = ImageTypeCheker(image)
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageToShow)
        .crossfade(true)
        .apply {
            if (image is ImageSourceType.Url) {
                diskCachePolicy(CachePolicy.ENABLED)
                memoryCachePolicy(CachePolicy.DISABLED)
                networkCachePolicy(CachePolicy.DISABLED)
            } else {
                // This is a Bitmap or local File: Skip caching
                diskCachePolicy(CachePolicy.DISABLED)
                memoryCachePolicy(CachePolicy.DISABLED)
                networkCachePolicy(CachePolicy.DISABLED)
            }
        }.error(ERROR_ID)
        .build()
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        modifier = Modifier // take all free space
            .fillMaxWidth()
            .heightIn(min = minHeight.dp, max = maxHeight.dp)  // NEVER shrink below 600dp
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                imageRequest
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun MediumImageBox(
    image: ImageSourceType,
    modifier: Modifier = Modifier,
    onImageClick: (ImageSourceType) -> Unit
) {
    val imageToShow = ImageTypeCheker(image)
    val sizeDp = 120.dp

    // Track image loading state
    var isImageLoaded by remember { mutableStateOf(false) }
    var isImageLoading by remember { mutableStateOf(true) }

    val imageCardModifier = modifier
        .width(sizeDp)
        .height(sizeDp)
        .clickable(enabled = isImageLoaded) { // Only enable click when loaded
            onImageClick(image)
        }

    Card(
        modifier = imageCardModifier,
        shape = RoundedCornerShape(12.dp),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            // Show loading indicator while image is loading
            if (isImageLoading) {
                Icon(
                    painter = painterResource(R.drawable.outline_photo_library_24),
                    contentDescription = "Loading",
                    tint = Color.LightGray,
                    modifier = Modifier.size(46.dp)
                )

            }

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageToShow)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .networkCachePolicy(CachePolicy.ENABLED)
                        .error(ERROR_ID)
                        .listener(
                            onSuccess = { _, _ ->
                                isImageLoaded = true
                                isImageLoading = false
                            },
                            onError = { _, _ ->
                                isImageLoaded = false
                                isImageLoading = false
                            }
                        )
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

