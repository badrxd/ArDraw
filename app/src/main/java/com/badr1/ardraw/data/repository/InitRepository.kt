package com.badr1.ardraw.data.repository


import android.content.Context
import androidx.datastore.preferences.core.edit
import com.badr1.ardraw.DATA_VERSION
import com.badr1.ardraw.Graph.database
import com.badr1.ardraw.data.Category
import com.badr1.ardraw.data.Subcategory
import com.badr1.ardraw.data.dao.CategoriesDao
import com.badr1.ardraw.data.dao.SubcategoriesDao
import com.badr1.ardraw.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import android.util.Log
import com.google.common.reflect.TypeToken
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume


inline fun <reified T> Gson.fromJsonList(json: String): List<T> {
    val type = object : TypeToken<List<T>>() {}.type
    return fromJson(json, type)
}

class InitRepository(
    private val categoriesDao: CategoriesDao,
    private val subcategoriesDao: SubcategoriesDao,
    private val context: Context // Keep if absolutely necessary, but minimize use
) {

    private val _initDone = MutableStateFlow(false)
    val initDone = _initDone.asStateFlow()

    private val REMOTE_CONFIG_VERSION_KEY = "data_version"

    /**
     * Orchestrates the data initialization and update flow.
     */
    suspend fun insertDataIfNeeded() {
        val localVersion = getLocalDataVersion()

        // 1. CHECK REMOTE VERSION & HANDLE FAILURE (Separation of Concerns)
        val remoteVersion = try {
            checkRemoteVersion()
        } catch (e: Exception) {
            // This is the CRITICAL EXIT POINT for network failure.
            handleRemoteCheckFailure(e, localVersion)
            return
        }
        Log.d("InitRepository", "Remote version 1: $remoteVersion")
        Log.d("InitRepository", "Local version 2: $localVersion")


        // 2. DECISION LOGIC: Proceed only if an update is required
        if (remoteVersion <= localVersion) {
            println("Local data version ($localVersion) is current.")
        } else {
            println("Remote version ($remoteVersion) is newer than local version ($localVersion). Updating database...")

            Log.d("InitRepository", "Remote version 2: $remoteVersion")

            // 3. FETCH AND PERSIST NEW DATA
            fetchAndPersistNewData(remoteVersion)
        }

        _initDone.value = true // All checks complete.
    }

    // --- HELPER METHODS FOR CLARITY ---

    private suspend fun getLocalDataVersion(): Int {
        // Use the injected dataStore instance
        return context.dataStore.data.first()[DATA_VERSION] ?: 0
    }

    private suspend fun checkRemoteVersion(): Int {
        val remoteConfig = Firebase.remoteConfig

        // --- RESTORE THIS BLOCK FOR DEVELOPMENT ---
        val configSettings = remoteConfigSettings {
            // Set to 0 to force a fetch every time the function is called.
            // **IMPORTANT:** Reset this to a longer interval (e.g., 3600 seconds)
            // before releasing to production to avoid unnecessary network costs.
            minimumFetchIntervalInSeconds = 3600L
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        // ------------------------------------------

        // Fetch and activate the configuration
        remoteConfig.fetchAndActivate().await()

        // Check the fetched value
        val newVersion = remoteConfig.getLong(REMOTE_CONFIG_VERSION_KEY).toInt()
        println("Remote Config fetched version: $newVersion")

        return newVersion
    }

    private fun handleRemoteCheckFailure(e: Exception, localVersion: Int) {
        // Logs the failure and informs the UI that the initialization logic is "done"
        // (meaning: we finished checking, and the result is current local data or no data).
        println("Remote Config fetch failed: ${e.message}. Using local data version: $localVersion")
        _initDone.value = true
    }

    private suspend fun fetchAndPersistNewData(remoteVersion: Int) {
        val jsonConfig = try {
            fetchDataFromFirebase()
        } catch (e: Exception) {
            println("Failed to fetch full data from Realtime Database: ${e.message}. Retaining old local data.")
            return // Exit update if full DB fetch fails
        }
        Log.d("InitRepository", "data from database: $jsonConfig")

        if (jsonConfig != null) {
            // 1. Clear database (Use transaction wrapper for safety if needed)
            withContext(Dispatchers.IO) {

                database.clearAllTables()

                // 2. Insert new data (The function is kept clean)
                insertDataInDatabase(jsonConfig)
            }
            // 3. Update DataStore version
            context.dataStore.edit { prefs ->
                prefs[DATA_VERSION] = remoteVersion
            }
            println("Database successfully updated to version $remoteVersion.")
        } else {
            // Handle case where fetch was successful but returned null data
            println("Remote database node was empty or null. Aborting update.")
        }
    }

    private suspend fun fetchDataFromFirebase(): String? =
        suspendCancellableCoroutine { continuation ->
            val ref = FirebaseDatabase.getInstance().getReference("categories")

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firebaseValue = snapshot.value

                    Log.d("InitRepository", "firebaseValue: $firebaseValue")

                    if (firebaseValue != null) {
                        // 1. Convert the Java/Firebase Map/List structure into a valid JSON string
                        val gson = Gson()
                        val jsonString = gson.toJson(firebaseValue) // <-- THIS IS THE KEY CHANGE

                        // Log the clean JSON to verify it
                        println("Successfully serialized clean JSON: $jsonString")

                        continuation.resume(jsonString)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Network failure or permission denied
                    continuation.resume(null)
                }
            })
        }

    private suspend fun insertDataInDatabase(jsonString: String) {
        val gson = Gson()

        // Parse using JsonParser to avoid TypeToken issues
        val jsonArray = JsonParser.parseString(jsonString).asJsonArray

        val configList = mutableListOf<ConfigItem>()
        for (jsonElement in jsonArray) {
            val item = gson.fromJson(jsonElement, ConfigItem::class.java)
            configList.add(item)
        }

        val subCategoryList = mutableListOf<Subcategory>()

        for (item in configList) {
            val category = item.category
            val categoryId = categoriesDao.addCategory(Category(category = category))

            for (subName in item.subCategories) {
                subCategoryList.add(Subcategory(categoryId = categoryId, subcategory = subName))
            }
        }
        subcategoriesDao.addSubcategories(subCategoryList)
    }
}

data class ConfigItem(
    @SerializedName("category")
    val category: String,
    @SerializedName("subCategories")
    val subCategories: List<String>
)

