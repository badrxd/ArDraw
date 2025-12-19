
package com.badr1.ardraw.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.badr1.ardraw.Graph
import com.badr1.ardraw.data.Category
import com.badr1.ardraw.data.Subcategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class ImagesByCategory : ViewModel() {
    private val _categories = mutableStateListOf<Category>()
    val categories: List<Category> = _categories

    private val _subcategoryByCategory = mutableStateListOf<Subcategory>()
    val subcategoryByCategory: List<Subcategory> = _subcategoryByCategory

    private val _imagesDisplayingControl = mutableStateListOf<ImagesDisplayingControl>()
    val imagesDisplayingControl: List<ImagesDisplayingControl> = _imagesDisplayingControl

    private val _imagesUrl = mutableStateListOf<String>()
    val imagesUrl: List<String> = _imagesUrl

    val path: MutableState<String> = mutableStateOf("")

    private val _selectedCategory = mutableStateOf(Category())
    val selectedCategory: State<Category> = _selectedCategory

    private val _selectedSubcategory = mutableStateOf("")
    val selectedSubcategory: State<String> = _selectedSubcategory

    // Add loading and error states
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private var initJob: Job? = null

    fun initGetDatabase() {
        // Cancel any existing initialization
        initJob?.cancel()

        initJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // Wait for initialization with timeout
                val initialized = withTimeoutOrNull(10000L) {
                    Graph.InitRepository.initDone
                        .filter { it }
                        .first()
                } ?: false

                if (initialized) {
                    Log.d("AppDebug", "Database initialized")
                    getAllCategories()

                    if (_categories.isNotEmpty()) {
                        val category = _categories.first()
                        withContext(Dispatchers.Main) {
                            _selectedCategory.value = category
                        }
                        helper(category)
                    } else {
                        _errorMessage.value = "No categories available"
                    }
                } else {
                    Log.e("AppDebug", "Database initialization timeout")
                    _errorMessage.value = "Failed to initialize. Check your connection."

                    // Still try to load local data
                    getAllCategories()
                    if (_categories.isNotEmpty()) {
                        val category = _categories.first()
                        withContext(Dispatchers.Main) {
                            _selectedCategory.value = category
                        }
                        helper(category)
                    }
                }

                withContext(Dispatchers.Main) {
                    Log.d("AppDebug", "Final Control List: $_imagesDisplayingControl")
                }

            } catch (e: Exception) {
                Log.e("AppDebug", "Error in initGetDatabase: ${e.message}", e)
                _errorMessage.value = "Error loading data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun helper(category: Category) {
        try {
            getAllSubcategories(category.id)

            withContext(Dispatchers.Main) {
                _imagesDisplayingControl.clear()
            }

            Log.d("SubcategoryBox", "Loading subcategories: ${_subcategoryByCategory.size}")

            if (_subcategoryByCategory.isEmpty()) {
                Log.w("SubcategoryBox", "No subcategories found for category: ${category.category}")
                return
            }

            coroutineScope {
                val tasks = _subcategoryByCategory.map { subcategory ->
                    async(Dispatchers.IO) {
                        val path = "${category.category}/${subcategory.subcategory}"
                        val imageUrls = getImagesUrls(path)
                        Log.d("SubcategoryBox", "Loaded ${imageUrls.size} images for: ${subcategory.subcategory}")
                        ImagesDisplayingControl(subcategory, imageUrls)
                    }
                }

                val controlList = tasks.awaitAll()

                withContext(Dispatchers.Main) {
                    _imagesDisplayingControl.clear()
                    _imagesDisplayingControl.addAll(controlList)
                }

                Log.d("SubcategoryBox", "Total controls loaded: ${controlList.size}")
            }
        } catch (e: Exception) {
            Log.e("SubcategoryBox", "Error in helper: ${e.message}", e)
            _errorMessage.value = "Error loading subcategories"
        }
    }

    fun onCategoryChanged(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                withContext(Dispatchers.Main) {
                    _selectedCategory.value = category
                }
                helper(category)
            } catch (e: Exception) {
                Log.e("AppDebug", "Error changing category: ${e.message}", e)
                _errorMessage.value = "Error loading category"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getImagesUrls(path: String): List<String> {
        return try {
            // First, try to get from local database
            var imagesList = Graph.ImageMetadataRepository.getAllImagesByPath(path)

            if (imagesList.isEmpty()) {
                Log.d("ImageUrls", "No local images for $path, trying Firebase...")

                // Try to fetch from Firebase with timeout
                val images = withTimeoutOrNull(5000L) {
                    Graph.FirebaseRepository.getImageUrls(path)
                } ?: emptyList()

                if (images.isNotEmpty()) {
                    // Cache the images locally
                    Graph.ImageMetadataRepository.insertAllImages(path, images)
                    imagesList = images
                    Log.d("ImageUrls", "Fetched and cached ${images.size} images from Firebase")
                } else {
                    Log.w("ImageUrls", "No images found in Firebase for $path (might be offline)")
                }
            } else {
                Log.d("ImageUrls", "Loaded ${imagesList.size} images from local cache")
            }

            imagesList
        } catch (e: Exception) {
            Log.e("ImageUrls", "Error getting images for $path: ${e.message}", e)
            // Return local data even if Firebase fails
            Graph.ImageMetadataRepository.getAllImagesByPath(path)
        }
    }

    suspend fun getAllCategories() {
        try {
            val categories = Graph.CategoryRepository.getAllCategories()
            withContext(Dispatchers.Main) {
                _categories.clear()
                _categories.addAll(categories)
            }
            Log.d("Categories", "Loaded ${categories.size} categories")
        } catch (e: Exception) {
            Log.e("Categories", "Error loading categories: ${e.message}", e)
            throw e
        }
    }

    suspend fun getAllSubcategories(id: Long) {
        try {
            val subcategories = Graph.SubcategoryRepository.getSubcategoriesByCategory(id)
            withContext(Dispatchers.Main) {
                _subcategoryByCategory.clear()
                _subcategoryByCategory.addAll(subcategories)
            }
            Log.d("Subcategories", "Loaded ${subcategories.size} subcategories for category $id")
        } catch (e: Exception) {
            Log.e("Subcategories", "Error loading subcategories: ${e.message}", e)
            throw e
        }
    }
}

data class ImagesDisplayingControl(
    val subcategory: Subcategory,
    val images: List<String> = emptyList()
)