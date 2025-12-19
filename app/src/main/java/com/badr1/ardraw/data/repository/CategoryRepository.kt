package com.badr1.ardraw.data.repository

import com.badr1.ardraw.data.Category
import com.badr1.ardraw.data.dao.CategoriesDao

class CategoryRepository(private val categoriesDao: CategoriesDao) {
    suspend fun getAllCategories(): List<Category> {
        return categoriesDao.getAllCategories()
    }
    suspend fun countCategories(): Int {
        return categoriesDao.countCategories()
    }
}