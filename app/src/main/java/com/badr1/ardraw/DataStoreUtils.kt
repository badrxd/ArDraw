package com.badr1.ardraw

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// Top-level property: DataStore instance
val Context.dataStore by preferencesDataStore(name = "app_prefs")

// Key for first-launch flag
val DATA_VERSION = intPreferencesKey("data_version")


///////// Online Image /////////
const val GOOGLE_IMAGES_URL = "https://www.bing.com/images/search?q="
const val MOBILE_UA =
    "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36"


///////// FIREBASE /////////

