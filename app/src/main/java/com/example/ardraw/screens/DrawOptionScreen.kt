package com.example.ardraw.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ardraw.screens.components.Header
import com.example.ardraw.screens.components.ImageDisplayer
import androidx.compose.ui.graphics.Color

@Composable
fun DrawOptionScreen(modifier: Modifier, navController: NavController, url: String) {
    var selected = remember { mutableStateOf("Sketch") }
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Header("Select Option", navController)
        Spacer(modifier.height(24.dp))
        ImageDisplayer(url, 400)
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomSelectableButton(
                    text = "Sketch",
                    isSelected = selected.value == "Sketch",
                    modifier = Modifier.weight(1f)
                ) { selected.value = "Sketch" }

                CustomSelectableButton(
                    text = "Trace",
                    isSelected = selected.value == "Trace",
                    modifier = Modifier.weight(1f)
                ) { selected.value = "Trace" }
            }
            Button(
                onClick = { navController.navigate("draw_image") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Draw Now")
            }
        }
    }
}

@Composable
fun CustomSelectableButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF1E88E5) else Color.Gray
    val textColor = if (isSelected) Color(0xFF1E88E5) else Color.DarkGray

    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,      // No background
            contentColor = textColor,               // Text color
        ),
        border = BorderStroke(2.dp, borderColor),   // Border color
        elevation = null                            // No shadow
    ) {
        Text(text)
    }
}