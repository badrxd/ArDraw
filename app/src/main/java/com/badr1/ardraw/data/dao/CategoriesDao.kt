package com.badr1.ardraw.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.badr1.ardraw.data.Category

@Dao
abstract class CategoriesDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    abstract suspend fun addCategory(categoryEntity: Category): Long

    @Query("SELECT * FROM `categories_table`")
    abstract suspend fun getAllCategories(): List<Category>

    @Query("SELECT COUNT(id) FROM `categories_table`")
    abstract suspend fun countCategories(): Int
}