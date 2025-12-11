package com.example.ardraw

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Graph.provide(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentVersion = 1
                Graph.InitRepository.insertDataIfNeeded(currentVersion)
            } catch (e: Exception) {
                Log.e("InitApp", "Error initializing data", e)
            }
        }
    }
}