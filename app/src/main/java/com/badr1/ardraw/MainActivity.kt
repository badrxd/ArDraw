package com.badr1.ardraw

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.badr1.ardraw.navigation.AppNavigation
import com.badr1.ardraw.ui.theme.ArDrawTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            val insets = WindowCompat.getInsetsController(window, window.decorView)
            insets.isAppearanceLightStatusBars = true
            setContent {
                ArDrawTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppNavigation(
                            modifier = Modifier
                                .padding(innerPadding)
                                .systemBarsPadding()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Fatal error during activity creation/setup", e)
        }

    }
}

