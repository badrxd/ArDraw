package com.example.ardraw.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ardraw.screens.DrawImageScreen
import com.example.ardraw.screens.DrawOptionScreen
import com.example.ardraw.screens.HomeScreen
import com.example.ardraw.screens.ImagePreviewScreen
import com.example.ardraw.screens.ImageType.ImageSource
import com.example.ardraw.screens.ImagesByCategoryScreen
import com.example.ardraw.viewmodels.ImagesByCategory
import java.net.URI

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val vm: ImagesByCategory = viewModel()
    NavHost(
        navController = navController, startDestination = Screen.HomeRoute.route
    ) {
        composable(Screen.HomeRoute.route) {
            HomeScreen(modifier, navController)
        }

        composable(Screen.ImagesByCategoryRoute.route) {
            ImagesByCategoryScreen(modifier, vm, navController)
        }

        composable(
            Screen.ImagePreviewRoute.route,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            if (url !== "") {
                ImagePreviewScreen(modifier, navController, url)
            }
        }
        composable(
            Screen.DrawOptionRoute.route,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            if (url !== "") {
                DrawOptionScreen(modifier, navController, url)
            }
        }
        composable(Screen.DrawImageRoute.route) {
            DrawImageScreen(modifier, ImageSource.Url("badr"))
        }
    }
}