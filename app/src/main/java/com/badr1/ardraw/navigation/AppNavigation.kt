package com.badr1.ardraw.navigation

import AppInitScreen
import InitViewModel
import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.badr1.ardraw.Graph
import com.badr1.ardraw.screens.DrawImageScreen
import com.badr1.ardraw.screens.DrawOptionScreen
import com.badr1.ardraw.screens.HomeScreen
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.OnlineImageSearchScreen
import com.badr1.ardraw.screens.SubcategoryImages
import com.badr1.ardraw.screens.TextScreen
import com.badr1.ardraw.screens.TraceScreen
import com.badr1.ardraw.screens.gallery.GalleryScreen
import com.badr1.ardraw.viewmodels.ImagesByCategory
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel

@SuppressLint("RestrictedApi")
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val vm: ImagesByCategory = viewModel()
    val vm2: SharedDrawImageViewModel = viewModel()
    val initViewModel: InitViewModel = viewModel()

    // Get activity context for showing ads
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val route = backStackEntry.destination.route
            if (route != Screen.InitRoute.route && route != Screen.SketchImageRoute.route) {
                if (Graph.shouldShowAd()) {
                    activity?.let {
                        Graph.interstitialAdManager.showAd(
                            activity = it,
                            onAdDismissed = { Graph.updateLastAdTime() },
                            onAdFailed = { Graph.updateLastAdTime() }
                        )
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.InitRoute.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(400))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300))
        }
    ) {

        composable(Screen.InitRoute.route) {
            AppInitScreen(initViewModel, navigateToMain = {
                navController.navigate(Screen.HomeRoute.route) {
                    popUpTo(Screen.InitRoute.route) { inclusive = true }
                }
            })
        }
        composable(Screen.HomeRoute.route) {
            HomeScreen(modifier, navController, vm, vm2)
        }


        composable(
            Screen.DrawOptionRoute.route,
        ) {
            val image = vm2.selectedImage.value ?: ImageSourceType.Url("")
            DrawOptionScreen(modifier, navController, image, vm2)
        }
        composable(Screen.SketchImageRoute.route) {
            val image = vm2.selectedImage.value ?: ImageSourceType.Url("")
            DrawImageScreen(image, navController)
        }
        composable(Screen.TraceImageRoute.route) {
            val image = vm2.selectedImage.value ?: ImageSourceType.Url("")
            TraceScreen(modifier, navController, image)
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
            SubcategoryImages(modifier, subcategory = title, imagesList, navController, vm2)
        }

        composable(Screen.OnlineImageSearchRoute.route) {
            OnlineImageSearchScreen(modifier, navController, vm2)
        }

        composable(Screen.TextRoute.route) {

            TextScreen(modifier, navController, vm2)
        }
        composable(Screen.GalleryRoute.route) {
            GalleryScreen(modifier, navController)
        }
    }
}