package com.badr1.ardraw.data.repository

import com.badr1.ardraw.data.ImageMetadata
import com.badr1.ardraw.data.dao.ImageMetadataDao

class ImageMetadataRepository(private val imageMetadataDao: ImageMetadataDao) {
    private val cacheDuration = 12 * 60 * 60 * 1000L // 1 hour
    suspend fun insertAllImages(path: String, urls: List<String>) {
        return imageMetadataDao.insertAllImages(
            ImageMetadata(
                path,
                urls,
                System.currentTimeMillis()
            )
        )
    }

    suspend fun getAllImagesByPath(path: String): List<String> {
        val cached = imageMetadataDao.getAllImagesByPath(path)

        if (cached != null && !isExpired(cached.updatedAt)) {
            return cached.urls
        }
        imageMetadataDao.removeImagesByPath(path)
        return emptyList()
    }

    private fun isExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > cacheDuration
    }

}