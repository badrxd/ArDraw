package com.badr1.ardraw.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.badr1.ardraw.screens.components.Header
import com.badr1.ardraw.screens.components.LargeImageBox
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.badr1.ardraw.R
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.components.RequestPermissions
import com.badr1.ardraw.ui.theme.PurpleBoxColor
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel

@Composable
fun DrawOptionScreen(
    modifier: Modifier,
    navController: NavController,
    image: ImageSourceType,
    vm2: SharedDrawImageViewModel
) {
    val permissions = mutableListOf(Manifest.permission.CAMERA)

// Storage handling
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            permissions += Manifest.permission.READ_MEDIA_IMAGES
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            permissions += Manifest.permission.READ_EXTERNAL_STORAGE
        }

        else -> {
            permissions += Manifest.permission.READ_EXTERNAL_STORAGE
            permissions += Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    }
    var proceedToNextScreen by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
    ) {
        Header("Sketch & Trace", navController)
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier.height(24.dp))
            LargeImageBox(image, 400)
            Spacer(modifier = Modifier.weight(1f))
            if (proceedToNextScreen) {
                RequestPermissions(
                    onAllGranted = {
                        proceedToNextScreen = false
//                        vm2.setSelectedImage(ImageSourceType.Url(url))
                        navController.navigate("draw_image")
                    },
                    onPermissionDenied = {
                        proceedToNextScreen = false
                    })
            }
            Column(modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomSelectableButton(
                        text = "Camera",
                        icon = R.drawable.rounded_photo_camera_24,
                        onClick = {
                            proceedToNextScreen = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    CustomSelectableButton(
                        text = "Canvas",
                        icon = R.drawable.outline_phone_android_24,
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }
//                if (proceedToNextScreen) {
//                    RequestPermissions(
//                        onAllGranted = {
//                            proceedToNextScreen = false
//                            vm2.setSelectedImage(ImageSourceType.Url(url))
//                            navController.navigate("draw_image")
//                        },
//                        onPermissionDenied = {
//                            proceedToNextScreen = false
//                        })
//                }
            }
        }
    }
}

@Composable
fun CustomSelectableButton(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),

        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = PurpleBoxColor
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = text,
                    tint = Color.White
                )
                Text(
                    text,
                    color = Color.White
                )
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}