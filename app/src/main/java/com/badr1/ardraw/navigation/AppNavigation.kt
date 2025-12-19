package com.badr1.ardraw.navigation

import AppInitScreen
import InitViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.badr1.ardraw.screens.DrawImageScreen
import com.badr1.ardraw.screens.DrawOptionScreen
import com.badr1.ardraw.screens.HomeScreen
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.ImagesByCategoryScreen
import com.badr1.ardraw.screens.OnlineImageSearchScreen
import com.badr1.ardraw.screens.SubcategoryImages
import com.badr1.ardraw.screens.TextScreen
import com.badr1.ardraw.viewmodels.ImagesByCategory
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val vm: ImagesByCategory = viewModel()
    val vm2: SharedDrawImageViewModel = viewModel()
    val initViewModel: InitViewModel = viewModel()

    NavHost(
        navController = navController, startDestination = Screen.InitRoute.route
    ) {

        composable(Screen.InitRoute.route) {
            AppInitScreen(initViewModel, navigateToMain = {
                navController.navigate(Screen.HomeRoute.route) {
                    popUpTo(Screen.InitRoute.route) { inclusive = true }
                }
            })
        }
        composable(Screen.HomeRoute.route) {
            HomeScreen(modifier, navController, vm)
        }

        composable(Screen.ImagesByCategoryRoute.route) {
            ImagesByCategoryScreen(modifier, vm, navController,vm2)
        }

        composable(
            Screen.DrawOptionRoute.route,
        ) {
            val image = vm2.selectedImage.value ?: ImageSourceType.Url("")
            DrawOptionScreen(modifier, navController, image, vm2)
        }
        composable(Screen.DrawImageRoute.route) {
            val image = vm2.selectedImage.value ?: ImageSourceType.Url("")
            DrawImageScreen(image, navController)
        }

        composable(
            Screen.SubcategoryImagesRoute.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("subcategory") ?: ""
            val images = vm.imagesDisplayingControl.find { it ->
                it.subcategory.subcategory == title

            }
            val imagesList = images?.images ?: emptyList()
            SubcategoryImages(modifier, subcategory = title, imagesList, navController)
        }

        composable(Screen.OnlineImageSearchRoute.route) {
            OnlineImageSearchScreen(modifier, navController, vm2)
        }

        composable(Screen.TextRoute.route) {
            TextScreen(modifier, navController)
        }
    }
}