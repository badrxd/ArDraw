package com.badr1.ardraw.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        // Must convert List<String> to a single string (e.g., JSON)
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String> {
        // Must convert the stored string (JSON) back to List<String>
        if (json == null) return emptyList()

        // CRITICAL: Type token is essential for generic types like List<String>
        val type = object : TypeToken<List<String>>() {}.type

        return gson.fromJson(json, type) ?: emptyList()
    }
}
