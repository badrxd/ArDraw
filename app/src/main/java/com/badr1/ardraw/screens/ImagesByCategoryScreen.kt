package com.badr1.ardraw.screens


import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.badr1.ardraw.data.Category
import com.badr1.ardraw.viewmodels.ImagesByCategory
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.badr1.ardraw.navigation.Screen
import com.badr1.ardraw.viewmodels.ImagesDisplayingControl
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import java.util.Locale
import com.badr1.ardraw.R
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.ui.theme.PurpleBoxColor
import com.badr1.ardraw.screens.components.Header
import com.badr1.ardraw.screens.components.MediumImageBox
import com.badr1.ardraw.ui.theme.CustomPurple
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel



@Composable
fun AutoImageSlider(
    images: List<String>, autoScrollInterval: Long = 3000L // 3 seconds delay
) {
    if (images.isEmpty()) return

    // Create a virtual list much larger than the actual image list to enable infinite loop illusion
    val virtualCount = Int.MAX_VALUE
    val initialPage = virtualCount / 2

    // Calculate the starting page based on the actual image list size
    val initialImageIndex = initialPage % images.size

    val pagerState = rememberPagerState(
        initialPage = initialPage, initialPageOffsetFraction = 0f
    ) {
        virtualCount // The total number of virtual pages
    }

    // --- Automatic Scrolling Logic ---
    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoScrollInterval)
            yield()

            val nextPage = pagerState.currentPage + 1

            // --- CRUCIAL CHANGE HERE: Add the animationSpec parameter ---
            pagerState.animateScrollToPage(
                page = nextPage,
                // Set the animation duration to 800 milliseconds (0.8 seconds)
                // This gives a nice, medium-slow transition.
                animationSpec = tween(durationMillis = 2000)
            )
            // --- END CRUCIAL CHANGE ---
        }
    }
    // --- End Automatic Scrolling Logic ---

    HorizontalPager(
        state = pagerState, modifier = Modifier.fillMaxWidth()
    ) { page ->
        // Use the modulus operator to map the virtual page index to the actual image list index
        val imageIndex = page % images.size
        val imageModel = images[imageIndex]

        val painter = rememberAsyncImagePainter(model = imageModel)

        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f) // Example aspect ratio (adjust as needed)
        )
    }
}

@Composable
fun CategoriesSlider(
    categories: List<Category>,
    selectedCategory: Category,
    onClick: (Category) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            CategoryBox(
                category = cat,
                isSelected = cat.id == selectedCategory.id,
                onClick = { onClick(cat) })
        }
    }
    Spacer(modifier = Modifier.width(2.dp))
}

@Composable
fun CategoryBox(
    category: Category, isSelected: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = if (isSelected) CustomPurple.copy(alpha = 0.2f) else Color.Transparent
            )
            .clickable(
                // This part removes the flicker/ripple
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = category.category.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            },
            color = if (isSelected) CustomPurple else Color.Gray,
            style = if (isSelected) MaterialTheme.typography.subtitle2 else MaterialTheme.typography.subtitle2.copy(
                fontWeight = FontWeight.Normal
            ),
        )
    }
}

@Composable
fun SubcategoriesBox(
    subcategories: List<ImagesDisplayingControl>,
    navController: NavController,
    onImageClick: (ImageSourceType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Log.d("SubcategoryBox", "Subcategories: $subcategories")
        if (subcategories.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No Images Found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 8.dp, bottom = 46.dp)
            ) {
                items(subcategories) { item ->
                    SubcategoryBox(item, navController = navController, onImageClick = { image ->
                        onImageClick(image)
                    })
                }
            }
        }
    }
}

@Composable
fun SubcategoryBox(
    subcategory: ImagesDisplayingControl,
    navController: NavController,
    onImageClick: (ImageSourceType) -> Unit
) {
    val title = subcategory.subcategory.subcategory

    Column(
        Modifier
            .fillMaxWidth()
//            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                },
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = {
                    navController.navigate(
                        Screen.SubcategoryImagesRoute.passSubcategory(
                            title
                        )
                    )
                },
                colors = ButtonDefaults.textButtonColors(contentColor = CustomPurple.copy(alpha = 0.7f))
            ) {
                Text("See All", style = MaterialTheme.typography.button)
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(subcategory.images) { index, item ->
                if (index < 4) {
                    MediumImageBox(
                        ImageSourceType.Url(item),
                        onImageClick = { image ->
                            onImageClick(image)
                        })
                } else if (index == 4) {
                    val imageCardModifier =
                        Modifier
                            .background(Color.Transparent)
                            .width(120.dp)
                            .height(120.dp)
//                            .padding(end = 12.dp)
                            .clickable {
                                navController.navigate(
                                    Screen.SubcategoryImagesRoute.passSubcategory(
                                        title
                                    )
                                )

                            }

                    val cornerRadiusDp = 12.dp
                    val borderWidth = 2.dp
                    val dashPattern = floatArrayOf(12f, 12f) // 8 units line, 8 units gap

                    Card(
                        modifier = imageCardModifier,
                        // Card's shape provides the clipping and visible background shape
                        shape = RoundedCornerShape(cornerRadiusDp),
                        elevation = 0.dp,
                        // ... (other Card parameters)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CustomPurple.copy(alpha = 0.1f))
                                .drawBehind {
                                    val strokeWidthPx = borderWidth.toPx()
                                    val halfStroke = strokeWidthPx / 2f

                                    val pathEffect = PathEffect.dashPathEffect(dashPattern, 0f)
                                    val strokeStyle = Stroke(
                                        width = strokeWidthPx, pathEffect = pathEffect
                                    )

                                    // Define the size and corner radius of the rectangle to draw
                                    val rectSize = Size(
                                        width = size.width - strokeWidthPx,
                                        height = size.height - strokeWidthPx
                                    )

                                    // Draw the rounded rectangle with the dashed stroke
                                    drawRoundRect(
                                        color = CustomPurple.copy(alpha = 0.7f),
                                        // Offset moves the rectangle inwards by half the stroke width
                                        topLeft = Offset(halfStroke, halfStroke),
                                        size = rectSize,
                                        // Use the corner radius from the Card's shape
                                        cornerRadius = CornerRadius(cornerRadiusDp.toPx()),
                                        style = strokeStyle
                                    )
                                }, contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(id = R.drawable.outline_photo_library_24),
                                    tint = CustomPurple.copy(alpha = 0.7f),
                                    contentDescription = "see more",
                                )
                                Text("See More", color = CustomPurple.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }
}

