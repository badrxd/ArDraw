package com.example.ardraw.data.repository

import android.content.Context
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

            listResult.items.map { it.downloadUrl.await().toString() }

        } catch (e: Exception) {
            emptyList()
        }
    }

}