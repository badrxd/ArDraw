package com.badr1.ardraw.data.repository

import com.badr1.ardraw.data.Subcategory
import com.badr1.ardraw.data.dao.SubcategoriesDao

class SubcategoryRepository(private val subcategoriesDao: SubcategoriesDao) {
    suspend fun getSubcategoriesByCategory(categoryId: Long): List<Subcategory> {
        return subcategoriesDao.getSubcategoriesByCategory(categoryId)
    }
}