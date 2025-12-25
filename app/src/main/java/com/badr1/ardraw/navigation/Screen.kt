package com.badr1.ardraw.navigation

sealed class Screen(val route: String) {

    // ROOT
    object InitRoute : Screen("init_screen")
    object HomeRoute : Screen("home_screen")
    object ImagesByCategoryRoute : Screen("images_by_categories_screen")
    object DrawOptionRoute : Screen("draw_option")
    object SketchImageRoute : Screen("draw_image")
    object TraceImageRoute : Screen("trace_image")
    object SubcategoryImagesRoute : Screen("category_images/{subcategory}") {
        fun passSubcategory(subcategory: String): String = "category_images/${subcategory}"
    }

    object OnlineImageSearchRoute : Screen("online_image_search")

    ///// Text Screen
    object TextRoute : Screen("text_screen")


    ///// Gallery
    object GalleryRoute : Screen("gallery_screen")

}
