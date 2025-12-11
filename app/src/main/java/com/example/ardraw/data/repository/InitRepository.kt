package com.example.ardraw.data.repository


import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.ardraw.DATA_VERSION
import com.example.ardraw.Graph.database
import com.example.ardraw.data.Category
import com.example.ardraw.data.Subcategory
import com.example.ardraw.data.dao.CategoriesDao
import com.example.ardraw.data.dao.SubcategoriesDao
import com.example.ardraw.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import org.json.JSONArray

class InitRepository(
    private val categoriesDao: CategoriesDao,
    private val subcategoriesDao: SubcategoriesDao,
    private val context: Context
) {

    private val _initDone = MutableStateFlow(false)
    val initDone = _initDone.asStateFlow()
    suspend fun insertDataIfNeeded(currentVersion: Int) {
        val savedVersion = context.dataStore.data.first()[DATA_VERSION] ?: 0

        if (savedVersion < currentVersion) {
            // Update database
            database.clearAllTables()

            insertDataInDatabase()

            // Save new version
            context.dataStore.edit { prefs ->
                prefs[DATA_VERSION] = currentVersion
            }
        }
        _initDone.value = true
    }


    private suspend fun insertDataInDatabase() {
        val jsonString =
            context.assets.open("data/data.json").bufferedReader().use { it.readText() }
        val subCategoryList = mutableListOf<Subcategory>()

        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val category = obj.getString("category")
            val categoryId = categoriesDao.addCategory(Category(category = category))
            val subcategories = obj.getJSONArray("subCategories")
            for (j in 0 until subcategories.length()) {
                val subName = subcategories.getString(j)
                subCategoryList.add(Subcategory(categoryId = categoryId, subcategory = subName))
            }
        }
        subcategoriesDao.addSubcategories(subCategoryList)
    }
}