package com.example.ardraw.navigation

import android.net.Uri

sealed class Screen(val route: String) {

    // ROOT
    object HomeRoute : Screen("home_screen")
    object ImagesByCategoryRoute : Screen("images_by_categories_screen")

    object ImagePreviewRoute : Screen("image_preview/{url}") {
        fun passUrl(url: String): String = "image_preview/${Uri.encode(url)}"
    }

    object DrawOptionRoute : Screen("draw_option/{url}"){
        fun passUrl(url: String): String = "draw_option/${Uri.encode(url)}"
    }

    object DrawImageRoute : Screen("draw_image")
}

//yScreen : Screen("images_by_categories_screen")