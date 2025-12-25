package com.badr1.ardraw.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.badr1.ardraw.screens.components.Header
import com.badr1.ardraw.screens.components.LargeImageBox
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.badr1.ardraw.R
import com.badr1.ardraw.navigation.Screen
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.components.BannerAdView
import com.badr1.ardraw.screens.components.RequestPermissions
import com.badr1.ardraw.ui.theme.CustomBlue
import com.badr1.ardraw.ui.theme.CustomBrown
import com.badr1.ardraw.ui.theme.CustomPurple
import com.badr1.ardraw.ui.theme.PurpleBoxColor
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel

@Composable
fun DrawOptionScreen(
    modifier: Modifier,
    navController: NavController,
    image: ImageSourceType,
    vm2: SharedDrawImageViewModel
) {
    val permissions = listOf("camera")
    var proceedToNextScreen by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Sketch & Trace", navController)
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier.height(24.dp))
            LargeImageBox(image, 300,360)
            BannerAdView(
                modifier = modifier.padding(vertical = 16.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            if (proceedToNextScreen) {
                RequestPermissions(
                    permissions = permissions,
                    onAllGranted = {
                        proceedToNextScreen = false
                        vm2.setSelectedImage(image)
                        navController.navigate("draw_image")
                    },
                    onPermissionDenied = {
                        proceedToNextScreen = false
                    })
            }
            Column(modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomSelectableButton(
                        title = "Camera",
                        icon = R.drawable.rounded_photo_camera_24,
                        imageRes = R.drawable.custom_selectable_button_2,
                        onClick = {
                            proceedToNextScreen = true
                        },
                        modifier = Modifier.weight(1f),
                        color = CustomBlue
                    )
                    CustomSelectableButton(
                        title = "Canvas",
                        icon = R.drawable.outline_phone_android_24,
                        imageRes = R.drawable.custom_selectable_button_1,
                        onClick = {
                            navController.navigate(Screen.TraceImageRoute.route)
                        },
                        modifier = Modifier.weight(1f),
                        color = CustomBrown
                    )
                }
            }
        }
    }
}

@Composable
fun CustomSelectableButton(
    title: String,
    icon: Int,
    onClick: () -> Unit,
    backgroundColor: Color = Color.White,
    imageRes: Int,
    modifier: Modifier,
    color: Color
) {
    Card(
        modifier = modifier
//            .width(200.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
//                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = color
                )
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun DrawOptionScreenPreview() {
    DrawOptionScreen(
        modifier = Modifier,
        navController = NavController(LocalContext.current),
        image = ImageSourceType.Url("https://picsum.photos/200"),
        vm2 = SharedDrawImageViewModel()
    )
}