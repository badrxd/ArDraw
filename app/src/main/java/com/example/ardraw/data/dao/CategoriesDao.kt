package com.example.ardraw.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ardraw.data.Category

@Dao
abstract class CategoriesDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    abstract suspend fun addCategory(categoryEntity: Category): Long

    @Query("SELECT * FROM `categories_table`")
    abstract suspend fun getAllCategories(): List<Category>
}