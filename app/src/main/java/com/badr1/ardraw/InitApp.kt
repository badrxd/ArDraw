package com.badr1.ardraw

import android.app.Application
import android.util.Log
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.auth.FirebaseAuth

class InitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnSuccessListener {
                    Log.d("Auth bard", "✅ Anonymous sign-in successful")
                }
                .addOnFailureListener { e ->
                    Log.e("Auth badr", "❌ Anonymous sign-in failed: ${e.message}")
                }
        }
        MobileAds.initialize(this) {}
        Graph.provide(this)
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .memoryCache {
                    MemoryCache.Builder(this)
                        .maxSizePercent(0.15) // 15% RAM
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.resolve("image_cache"))
                        .maxSizeBytes(100L * 1024 * 1024) // 200MB
                        .build()
                }
                .respectCacheHeaders(false) // 🔥 FORCE caching
                .build()
        )
    }
}