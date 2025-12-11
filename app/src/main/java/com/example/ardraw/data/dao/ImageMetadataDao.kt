package com.example.ardraw.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ardraw.data.ImageMetadata

@Dao
abstract class ImageMetadataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllImages(metadata: ImageMetadata)

    @Query("SELECT * FROM image_metadata_table WHERE path = :path")
    abstract suspend fun getAllImagesByPath(path: String): ImageMetadata?

    @Query("DELETE FROM image_metadata_table WHERE path = :path")
    abstract suspend fun  removeImagesByPath(path: String)
}