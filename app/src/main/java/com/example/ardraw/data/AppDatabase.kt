package com.example.ardraw.data


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ardraw.data.dao.CategoriesDao
import com.example.ardraw.data.dao.ImageMetadataDao
import com.example.ardraw.data.dao.SubcategoriesDao

@Database(
    entities = [Category::class, Subcategory::class, ImageMetadata::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun CategoriesDao(): CategoriesDao
    abstract fun SubcategoriesDao(): SubcategoriesDao
    abstract fun ImageMetadataDao(): ImageMetadataDao
}