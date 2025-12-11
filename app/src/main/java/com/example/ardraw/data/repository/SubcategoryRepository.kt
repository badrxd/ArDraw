package com.example.ardraw.data.repository

import com.example.ardraw.data.Subcategory
import com.example.ardraw.data.dao.SubcategoriesDao

class SubcategoryRepository(private val subcategoriesDao: SubcategoriesDao) {
    suspend fun getSubcategoriesByCategory(categoryId: Long): List<Subcategory> {
        return subcategoriesDao.getSubcategoriesByCategory(categoryId)
    }
}