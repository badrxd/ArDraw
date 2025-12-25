package com.badr1.ardraw.screens

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.badr1.ardraw.R
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.components.Header
import com.badr1.ardraw.ui.theme.CustomPurple
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel
import com.google.common.math.Quantiles.scale
import java.time.temporal.TemporalQueries.offset


@Composable
fun TraceScreen(modifier: Modifier, navController: NavController, source: ImageSourceType) {

    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var alpha by remember { mutableFloatStateOf(0.8f) }
    var locked by remember { mutableStateOf(false) }
    var flipX by remember { mutableFloatStateOf(1f) } // 1f = normal, -1f = flipped

    Box(
        Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (!locked) {
                        Modifier.pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotate ->
                                offset += pan
                                scale = (scale * zoom).coerceIn(0.2f, 5f)
                                rotation += rotate
                            }
                        }
                    } else Modifier // 🔒 gestures disabled when locked
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = when (source) {
                    is ImageSourceType.Url -> source.url
                    is ImageSourceType.UriSourceType -> source.uri
                    is ImageSourceType.BitMap -> source.bitmap

                },
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = offset.x
                        translationY = offset.y
                        scaleX = scale * flipX
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()).padding(bottom = 8.dp),
            horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header(
                text = "",
                navController = navController,
                next = false
            )
            LookImage(locked, onLock = { locked = !locked })
        }
    }
}

@Composable
fun LookImage(locked: Boolean, onLock: () -> Unit) {

//    val background = if (locked) Color.LightGray else Color.White
    val color = if (locked) CustomPurple else Color.White


    Card(
        modifier = Modifier
            .size(46.dp)
            .offset(x = 8.dp), // The size of the outer card
        shape = RoundedCornerShape(100), // Optional: for rounded corners
        elevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center, // Centers the icon inside the 60dp card
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = CustomPurple.copy(alpha = 0.2f),
//                                shape = CircleShape // Optional: makes the background circular
                )
                .clickable{
                    onLock()
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_lock_24),
                contentDescription = "Settings",
                modifier = Modifier
                    .size(29.dp) // The actual size of the icon
                ,
//                    tint = PurpleBoxColor.copy(alpha = 0.5f)
                tint = color
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun DrawTraceScreenPreview() {
    TraceScreen(
        modifier = Modifier,
        navController = NavController(LocalContext.current),
        source = ImageSourceType.Url("https://picsum.photos/200"),
    )
}
