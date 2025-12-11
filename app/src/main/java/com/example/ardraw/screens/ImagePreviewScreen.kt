package com.example.ardraw.screens

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ardraw.navigation.Screen
import com.example.ardraw.screens.components.Header
import com.example.ardraw.screens.components.ImageDisplayer

@Composable
fun ImagePreviewScreen(
    modifier: Modifier,
    navController: NavController,
    url: String,

    ) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Header("Image Preview", navController)
        Spacer(modifier.height(24.dp))
        ImageDisplayer(url, 500)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navController.navigate(Screen.DrawOptionRoute.passUrl(url)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Continue")
        }
    }
}