package com.badr1.ardraw.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val context: Context) {
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    suspend fun getImageUrls(path: String): List<String> {
        return try {
            val folderRef = storage.reference.child("images/$path")

            val listResult = folderRef.listAll().await()

            Log.d("InitRepository", "List Result for $path: ${listResult.items} images found.")
            listResult.items.map { it.downloadUrl.await().toString() }

        } catch (e: Exception) {
            Log.e("InitRepository", "Error getting image URLs: ${e.message}")
            emptyList()
        }
    }

}