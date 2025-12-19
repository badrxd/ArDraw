package com.badr1.ardraw.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.components.Header
import com.badr1.ardraw.screens.components.MediumImageBox
import java.util.Locale

@Composable
fun SubcategoryImages(
    modifier: Modifier,
    subcategory: String,
    images: List<String>,
    navController: NavController
) {
    val title = subcategory.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
    Column(modifier.fillMaxSize()) {
        Header(title, navController)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images) { image ->
                MediumImageBox(image = ImageSourceType.Url(image), onImageClick = {})
            }
        }
    }
}
