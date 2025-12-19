package com.badr1.ardraw

import android.content.Context
import androidx.room.Room
import com.badr1.ardraw.data.AppDatabase
import com.badr1.ardraw.data.repository.InitRepository
import com.badr1.ardraw.data.repository.CategoryRepository
import com.badr1.ardraw.data.repository.FirebaseRepository
import com.badr1.ardraw.data.repository.ImageMetadataRepository
import com.badr1.ardraw.data.repository.SubcategoryRepository
import kotlin.getValue


object Graph {
    lateinit var database: AppDatabase
    private lateinit var appContext: Context

    val InitRepository by lazy {
        InitRepository(
            categoriesDao = database.CategoriesDao(),
            subcategoriesDao = database.SubcategoriesDao(),
            context = appContext
        )
    }
    val CategoryRepository by lazy {
        CategoryRepository(
            categoriesDao = database.CategoriesDao(),
        )
    }

    val SubcategoryRepository by lazy {
        SubcategoryRepository(
            subcategoriesDao = database.SubcategoriesDao(),
        )
    }

    val ImageMetadataRepository by lazy {
        ImageMetadataRepository(
            imageMetadataDao = database.ImageMetadataDao()
        )
    }

    fun provide(context: Context) {
        appContext = context.applicationContext
        database = Room.databaseBuilder(context, AppDatabase::class.java, "ardraw.db").build()
    }

    val FirebaseRepository by lazy {
        FirebaseRepository(
            context = appContext
        )
    }
}