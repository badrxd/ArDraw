package com.example.ardraw.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ardraw.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.ardraw.ui.theme.BlueBoxColor
import com.example.ardraw.ui.theme.CyanBoxColor
import com.example.ardraw.ui.theme.PinkBoxColor
import com.example.ardraw.ui.theme.PurpleBoxColor
import com.example.ardraw.ui.theme.PurpleGradientStart


@Composable
fun HomeScreen(modifier: Modifier, navController: NavController) {
    val tools = listOf<DrawToolsList>(
        DrawToolsList(R.drawable.paint_palette, "Draw with Template", CyanBoxColor, route = "images_by_categories_screen"),
        DrawToolsList(R.drawable.paint_palette, "Draw with Gallery", PurpleBoxColor, route = ""),
        DrawToolsList(R.drawable.paint_palette, "Draw with Collection", BlueBoxColor, route = ""),
        DrawToolsList(R.drawable.paint_palette, "Text Art", PinkBoxColor, route = "")
    )
    Column(modifier.fillMaxSize()) {
        HomeHeader(modifier)
        Column(
            modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier.fillMaxWidth()) {
                Text("Tools", style = MaterialTheme.typography.h6, fontWeight = FontWeight.W800)
                Box(
                    modifier = Modifier
                        .width(45.dp) // same width as box
                        .height(8.dp)  // bar height
                        .clip(RoundedCornerShape(4.dp)) // rounded bar
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    PurpleGradientStart,
                                    Color.Transparent
                                ) // purple gradient
                            )
                        )
                )
            }
            Spacer(modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tools) { item ->
                    ToolsContainer(item, onClick = {
                        if (!item.route.isEmpty()) {
                            navController.navigate(item.route)
                        }
                    })
                }
            }

        }
    }
}


@Composable
fun ToolsContainer(item: DrawToolsList, onClick: () -> Unit) {
    Box(
        Modifier
            .clickable(
                onClick = { onClick() },
            )
            .fillMaxWidth()
            .height(170.dp)
            .shadow(
                elevation = 8.dp,        // shadow size
                shape = RoundedCornerShape(16.dp),
                clip = false              // do not clip content
            )
            .background(color = item.color, shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(100))
                    .background(color = Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = item.image),
                    contentDescription = "Title",
                    modifier = Modifier.size(80.dp), // adjust size as needed
                    contentScale = ContentScale.Crop // scale the image nicely
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                item.text,
                style = MaterialTheme.typography.subtitle2,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun HomeHeader(modifier: Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                elevation = 16.dp, // shadow size
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                ), // shape of the shadow
                clip = true // if true, content will be clipped to shape
            )
            .background(
                Color.Blue, shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            )
    ) {
        Row(
            Modifier.fillMaxSize()
        ) { }
        Image(
            painter = painterResource(id = R.drawable.app_banner),
            contentDescription = "App Banner Image",
            modifier = Modifier.fillMaxSize(), // adjust size as needed
            contentScale = ContentScale.Crop // scale the image nicely
        )
        Row(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .background(color = Color.Black)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }
    }
}

data class DrawToolsList(
    val image: Int = 0,
    val text: String = "",
    val color: Color = Color.White,
    val route: String = ""
)