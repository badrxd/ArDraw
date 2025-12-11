package com.example.ardraw.data.repository

import com.example.ardraw.data.Category
import com.example.ardraw.data.dao.CategoriesDao

class CategoryRepository(private val categoriesDao: CategoriesDao) {
    suspend fun getAllCategories(): List<Category> {
        return categoriesDao.getAllCategories()
    }
}