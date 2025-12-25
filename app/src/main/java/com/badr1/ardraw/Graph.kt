package com.badr1.ardraw

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.badr1.ardraw.data.AppDatabase
import com.badr1.ardraw.data.repository.InitRepository
import com.badr1.ardraw.data.repository.CategoryRepository
import com.badr1.ardraw.data.repository.FirebaseRepository
import com.badr1.ardraw.data.repository.ImageMetadataRepository
import com.badr1.ardraw.data.repository.SubcategoryRepository
import com.badr1.ardraw.screens.components.InterstitialAdManager
import kotlin.getValue
import androidx.core.content.edit


object Graph {
    lateinit var database: AppDatabase
    private lateinit var appContext: Context

    @SuppressLint("StaticFieldLeak")
    lateinit var interstitialAdManager: InterstitialAdManager
        private set
    private lateinit var sharedPreferences: SharedPreferences
    private const val AD_INTERVAL_MS = 3 * 60 * 1000L

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
        interstitialAdManager = InterstitialAdManager(context)
        interstitialAdManager.loadAd()
        sharedPreferences = context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE)

        if (!sharedPreferences.contains("last_ad_time")) {
            val pastTime = System.currentTimeMillis() - AD_INTERVAL_MS - 1000L // 3 min + 1 sec ago
            sharedPreferences.edit { putLong("last_ad_time", pastTime) }
        }
    }

    val FirebaseRepository by lazy {
        FirebaseRepository(
            context = appContext
        )
    }

    fun updateLastAdTime() {
        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit { putLong("last_ad_time", currentTime) }
    }

    fun shouldShowAd(): Boolean {
        val lastAdTime = sharedPreferences.getLong("last_ad_time", 0L)
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastAdTime

        return timeSinceLastAd >= AD_INTERVAL_MS
    }
}