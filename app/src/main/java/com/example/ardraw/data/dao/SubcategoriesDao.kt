package com.example.ardraw.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ardraw.data.Subcategory

@Dao
abstract class SubcategoriesDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    abstract suspend fun addSubcategories(categoryEntity: List<Subcategory>)

    @Query("SELECT * FROM `subcategories_table`")
    abstract suspend fun getAllSubcategories(): List<Subcategory>


    @Query("SELECT * FROM subcategories_table WHERE category_id = :categoryId")
    abstract suspend fun getSubcategoriesByCategory(categoryId: Long): List<Subcategory>

}