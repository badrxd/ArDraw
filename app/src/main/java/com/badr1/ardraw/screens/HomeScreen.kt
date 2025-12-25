package com.badr1.ardraw.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.badr1.ardraw.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.badr1.ardraw.Graph
import com.badr1.ardraw.navigation.Screen
import com.badr1.ardraw.screens.components.BannerAdView
import com.badr1.ardraw.ui.theme.BlueBoxColor
import com.badr1.ardraw.ui.theme.CustomBlue
import com.badr1.ardraw.ui.theme.CustomBrown
import com.badr1.ardraw.ui.theme.CustomPurple
import com.badr1.ardraw.ui.theme.CyanBoxColor
import com.badr1.ardraw.ui.theme.PinkBoxColor
import com.badr1.ardraw.ui.theme.PurpleBoxColor
import com.badr1.ardraw.ui.theme.PurpleGradientStart
import com.badr1.ardraw.viewmodels.ImagesByCategory
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel


@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: ImagesByCategory,
    vm2: SharedDrawImageViewModel
) {
    val selectedCategory by viewModel.selectedCategory
    val imagesDisplayingControl = viewModel.imagesDisplayingControl

    val context = LocalContext.current
    val activity = context as? Activity
    val interstitialAdManager = Graph.interstitialAdManager

    LaunchedEffect(Unit) {
        if (viewModel.categories.isEmpty()) {
            viewModel.initGetDatabase()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Ar Drawing",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.W800
                )
            }
            Spacer(modifier.height(4.dp))
            Card(
                modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.main_screen),
                        contentDescription = "App Banner Image",
                        modifier = modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f), // adjust size as needed
                        contentScale = ContentScale.Crop // scale the image nicely
                    )
                }
            }
            Spacer(modifier.height(16.dp))
            Row(
                modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                NavIcons(
                    modifier,
                    color = CustomPurple,
                    icon = R.drawable.browser,
                    text = "Browser",
                    onClick = { navController.navigate(Screen.OnlineImageSearchRoute.route) })

                Spacer(modifier.width(56.dp))
                NavIcons(
                    modifier,
                    color = CustomBlue,
                    icon = R.drawable.outline_insert_text_24,
                    text = "Text",
                    onClick = {
                        navController.navigate(Screen.TextRoute.route)
                    }
                )
                Spacer(modifier.width(56.dp))
                NavIcons(
                    modifier,
                    color = CustomBrown,
                    icon = R.drawable.baseline_folder_24,
                    text = "Saved",
                    onClick = { navController.navigate(Screen.GalleryRoute.route) }
                )
            }
            Spacer(modifier.height(8.dp))
            Divider()
            Spacer(modifier.height(8.dp))
            CategoriesSlider(viewModel.categories, selectedCategory, onClick = { cat ->
                viewModel.onCategoryChanged(cat)
            })
            if (viewModel.isLoading.value) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = CustomPurple.copy(alpha = 0.5f)
                    )
                }
            }

            viewModel.errorMessage.value?.let { error ->
                Text(text = error, color = Color.Red)
            }
//        if (imagesDisplayingControl.isNotEmpty()) {
            SubcategoriesBox(imagesDisplayingControl, navController, onImageClick = { image ->
                vm2.setSelectedImage(image)
                navController.navigate(Screen.DrawOptionRoute.route)
            })
//        }
        }
        BannerAdView(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun NavIcons(modifier: Modifier, color: Color, icon: Int, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Card(
            modifier = modifier.size(56.dp), // The size of the outer card
            shape = RoundedCornerShape(16.dp) // Optional: for rounded corners
        ) {
            Box(
                contentAlignment = Alignment.Center, // Centers the icon inside the 60dp card
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = color.copy(alpha = 0.2f),
//                                shape = CircleShape // Optional: makes the background circular
                    )
                    .clickable {
                        onClick()
                    }
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(24.dp) // The actual size of the icon
                    ,
                    tint = color
                )
            }
        }
        Text(text, style = MaterialTheme.typography.subtitle2, fontWeight = FontWeight.W400)
    }
}

//@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
//@Composable
//fun HomeScreenPreview(modifier: Modifier = Modifier) {
//    HomeScreen(modifier, NavController(LocalContext.current))
//}