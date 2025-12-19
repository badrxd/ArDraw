package com.badr1.ardraw.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_metadata_table")
data class ImageMetadata(
    @PrimaryKey val path: String,
    @ColumnInfo(name = "urls")
    val urls: List<String>,
    @ColumnInfo(name = "insert_time")
    val updatedAt: Long
)