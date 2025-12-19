import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.badr1.ardraw.Graph.InitRepository
import com.badr1.ardraw.Graph.database

sealed class InitState {
    object Loading : InitState()            // Currently checking/downloading
    object Success : InitState()            // Data is ready (go to main screen)
    object WaitingForInternet :
        InitState() // Used specifically for mandatory first-time load failure
    object GenericError : InitState()       // Non-critical or unexpected error
}

class InitViewModel() : ViewModel() {

    private val _initState = MutableStateFlow<InitState>(InitState.Loading)
    val initState: StateFlow<InitState> = _initState.asStateFlow()

    init {
        checkAndLoadData()
    }

    fun checkAndLoadData() {
        viewModelScope.launch {
            _initState.value = InitState.Loading
            Log.d("InitRepository", "Checking and loading data...")

            // 1. Determine if this is a true first run (no local data exists yet)
            val hasLocalData = database.CategoriesDao().countCategories() > 0

            Log.d("InitRepository", "Has local data: $hasLocalData")

            // 2. Trigger the repository logic
            InitRepository.insertDataIfNeeded()

            Log.d("InitRepository", "Data loading complete.")

            // 3. Wait for the repository to complete its check/update cycle
            InitRepository.initDone.collect { isDone ->
                if (isDone) {
                    val finalCount = database.CategoriesDao().countCategories()
                    Log.d("InitRepository", "Final count: $finalCount")

                    when {
                        // Case C, B, D: Data is available (either loaded new or retrieved from cache)
                        finalCount > 0 -> {
                            _initState.value = InitState.Success
                        }
                        // Case A: First Install, NO Local Data, AND Failed to Download
                        !hasLocalData && finalCount == 0 -> {
                            // The repository returned without data AND there was no old data
                            _initState.value = InitState.WaitingForInternet
                        }
                        // Fallback for unexpected empty state after successful run
                        else -> {
                            _initState.value = InitState.GenericError
                        }
                    }
                }
            }
        }
    }
}