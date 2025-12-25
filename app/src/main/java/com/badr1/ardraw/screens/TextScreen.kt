package com.badr1.ardraw.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.badr1.ardraw.navigation.Screen
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.components.BannerAdView
import com.badr1.ardraw.screens.components.Header
import com.badr1.ardraw.ui.theme.PurpleBoxColor
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel
import com.badr1.ardraw.viewmodels.TextViewModel

@Composable
fun TextScreen(modifier: Modifier, navController: NavController, vm2: SharedDrawImageViewModel) {

    val viewModel: TextViewModel = viewModel()
    val selectedFont = viewModel.selectedFont.value
    val context = LocalContext.current
    val text = viewModel.text.value

    val typeface = remember(selectedFont) {
        ResourcesCompat.getFont(context, selectedFont)
            ?: error("Font not found")
    }

    val bitmap = remember(text, typeface) {
        viewModel.fontToBitmap(typeface)
    }
    Column(modifier.fillMaxSize()) {
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Header("Text", navController, true, onNext = {
                vm2.setSelectedImage(ImageSourceType.BitMap(bitmap))
                navController.navigate(Screen.DrawOptionRoute.route)
            })
        }
        Column(
            modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(8.dp)) // Clip first
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp) // Add shape to border
                    ),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = text,
                    onValueChange = { viewModel.onChangeText(it) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.h3.copy(
                        fontFamily = FontFamily(Font(selectedFont)),
                        textAlign = TextAlign.Center
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = PurpleBoxColor
                    )
                )
            }
            BannerAdView(
                modifier = modifier
                    .fillMaxWidth().padding(vertical = 6.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .width((150.dp * 7) + (8.dp * 8)), // Fixed width forces horizontal scroll
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(viewModel.fontStyles) { item ->
                        TextBox(item, item == selectedFont) {
                            viewModel.selectFont(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextBox(textStyle: Int, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor =
        if (isSelected) PurpleBoxColor else Color.LightGray
    val textColor =
        if (isSelected) Color.White else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(
                RoundedCornerShape(8.dp),
            )
            .background(color = backgroundColor)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Ar Draw",
            fontFamily = FontFamily(Font(textStyle)),
            style = MaterialTheme.typography.subtitle1,
            color = textColor,
            modifier = Modifier.padding(1.dp)
        )
    }
}

