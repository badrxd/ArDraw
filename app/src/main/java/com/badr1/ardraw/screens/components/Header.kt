package com.badr1.ardraw.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Header(
    text: String,
    navController: NavController,
    next: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier
                .size(35.dp)
                .clickable(onClick = { navController.navigateUp() }),
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Go Back",
        )
        Spacer(Modifier.width(1.dp))
        Text(
            text,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        if (next) {
            Spacer(Modifier.width(1.dp))
            Button(onClick = { onClick() }, modifier = Modifier.padding(horizontal = 4 .dp)) {
                Text(text = "Done")
            }
        }
    }
}