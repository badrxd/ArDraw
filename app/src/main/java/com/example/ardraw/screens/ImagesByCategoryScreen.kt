package com.example.ardraw.screens

import android.R
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ardraw.data.Category
import com.example.ardraw.viewmodels.ImagesByCategory
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.ardraw.data.Subcategory
import androidx.compose.foundation.lazy.grid.items
import androidx.navigation.NavController
import com.example.ardraw.navigation.Screen

@Composable
fun ImagesByCategoryScreen(modifier: Modifier, vm: ImagesByCategory, navController: NavController) {

    val categories = remember { vm.categories }
    val selectedCategory by remember { vm.selectedCategory }

    val subcategories = remember { vm.subcategoryByCategory }
    val selectedSubcategory by remember { vm.selectedSubcategory }

    Column(modifier.fillMaxSize()) {
        SimpleSelectBar(categories, modifier, selectedCategory, onCategorySelected = { category ->
            if (selectedCategory != category.category) {
                vm.onCategoryChanged(category)
            }
        })
        Spacer(modifier.height(20.dp))
        SubcategorySlider(
            subcategories,
            selectedSubcategory,
            onSubcategorySelected = { subcategories ->
                if (selectedSubcategory !== subcategories.subcategory) {
                    vm.onSubcategoryChanged(subcategories)
                }
            })
        Spacer(modifier.height(40.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vm.imagesUrl) { url ->
                NetworkImage(
                    url = url,
                    modifier,
                    navController
                )

            }
        }
    }
}


@Composable
fun SimpleSelectBar(
    options: List<Category>,
    modifier: Modifier = Modifier,
    selectedCategory: String,
    onCategorySelected: (Category) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { expanded = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedCategory)

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select",
                modifier = Modifier.size(24.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onCategorySelected(item)
                        expanded = false
                    }
                ) {
                    Text(item.category)
                }
            }
        }
    }
}

@Composable
fun SubcategorySlider(
    subcategories: List<Subcategory>,
    selectedSubcategory: String,
    onSubcategorySelected: (Subcategory) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subcategories) { sub ->
            SubcategoryBox(
                subcategory = sub.subcategory,
                isSelected = sub.subcategory == selectedSubcategory,
                onClick = { onSubcategorySelected(sub) }
            )
        }
    }
}

@Composable
fun SubcategoryBox(
    subcategory: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color.Blue else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = subcategory,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NetworkImage(url: String, modifier: Modifier = Modifier, navController: NavController) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = {
                navController.navigate(Screen.ImagePreviewRoute.passUrl(url))
            }),
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .placeholder(R.drawable.stat_notify_error)
                        .error(R.drawable.stat_notify_error)
                        .build(),
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
