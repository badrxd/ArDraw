package com.example.ardraw.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ardraw.Graph
import com.example.ardraw.Graph.CategoryRepository
import com.example.ardraw.Graph.SubcategoryRepository
import com.example.ardraw.Graph.FirebaseRepository
import com.example.ardraw.Graph.ImageMetadataRepository
import com.example.ardraw.data.Category
import com.example.ardraw.data.Subcategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class ImagesByCategory() : ViewModel() {
    private val _categories = mutableStateListOf<Category>()
    val categories: List<Category> = _categories

    val test: MutableState<Boolean> = mutableStateOf(false)

    private val _subcategoryByCategory = mutableStateListOf<Subcategory>()
    val subcategoryByCategory: List<Subcategory> = _subcategoryByCategory

    private val _imagesUrl = mutableStateListOf<String>()
    val imagesUrl: List<String> = _imagesUrl

    val path: MutableState<String> = mutableStateOf("")

    private val _selectedCategory = mutableStateOf("")
    val selectedCategory: State<String> = _selectedCategory

    private val _selectedSubcategory = mutableStateOf("")
    val selectedSubcategory: State<String> = _selectedSubcategory

    init {
        viewModelScope.launch {
            Graph.InitRepository.initDone.filter { it }
                .take(1)
                .collect { done ->
                    if (done) {
                        getAllCategories()
                        val category = _categories.first()
                        _selectedCategory.value = category.category
                        getAllSubcategories(category.id)
                        if (_subcategoryByCategory.isNotEmpty()) {
                            _selectedSubcategory.value = _subcategoryByCategory.first().subcategory
                            val path = "${_selectedCategory.value}/${_selectedSubcategory.value}"
                            getImagesUrls(path)
                        }
                    }
                }
        }
    }

    fun onCategoryChanged(category: Category){
        _selectedCategory.value = category.category
        _imagesUrl.clear()
        viewModelScope.launch(Dispatchers.IO) {
            getAllSubcategories(category.id)
            _selectedSubcategory.value = _subcategoryByCategory.first().subcategory
            val path = "${_selectedCategory.value}/${_selectedSubcategory.value}"
            getImagesUrls(path)
        }
    }
    fun onSubcategoryChanged(subcategory: Subcategory) {
        _selectedSubcategory.value = subcategory.subcategory
        _imagesUrl.clear()
        viewModelScope.launch(Dispatchers.IO) {
            val path = "${_selectedCategory.value}/${_selectedSubcategory.value}"
            getImagesUrls(path)
        }
    }
    suspend fun getImagesUrls(path: String) {
        var imagesList = ImageMetadataRepository.getAllImagesByPath(path)
        if (imagesList.isEmpty()) {
            val images: List<String> =
                FirebaseRepository.getImageUrls(path)
            ImageMetadataRepository.insertAllImages(
                path,
                images
            )
            imagesList = ImageMetadataRepository.getAllImagesByPath(path)
        }
        _imagesUrl.clear()
        _imagesUrl.addAll(imagesList)
    }

    suspend fun getAllCategories() {
        val categories: List<Category> = CategoryRepository.getAllCategories()
        _categories.clear()
        _categories.addAll(categories)
    }

    suspend fun getAllSubcategories(id: Long) {

        val subcategories =
            SubcategoryRepository.getSubcategoriesByCategory(id)
        _subcategoryByCategory.clear()
        _subcategoryByCategory.addAll(subcategories)

    }
}