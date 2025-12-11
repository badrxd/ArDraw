package com.example.ardraw

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// Top-level property: DataStore instance
val Context.dataStore by preferencesDataStore(name = "app_prefs")

// Key for first-launch flag
val DATA_VERSION = intPreferencesKey("data_version")

