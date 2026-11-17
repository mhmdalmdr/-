package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.GroundingSource
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromGroundingSourceList(sources: List<GroundingSource>?): String {
        if (sources.isNullOrEmpty()) return "[]"
        val jsonArray = JSONArray()
        for (source in sources) {
            val jsonObject = JSONObject()
            jsonObject.put("title", source.title)
            jsonObject.put("uri", source.uri)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toGroundingSourceList(jsonString: String?): List<GroundingSource> {
        if (jsonString.isNullOrBlank() || jsonString == "[]") return emptyList()
        val list = mutableListOf<GroundingSource>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    GroundingSource(
                        title = obj.optString("title", ""),
                        uri = obj.optString("uri", "")
                    )
                )
            }
        } catch (_: Exception) {
            // Return empty list on parse error
        }
        return list
    }

    @TypeConverter
    fun fromStringList(strings: List<String>?): String {
        if (strings.isNullOrEmpty()) return "[]"
        val jsonArray = JSONArray()
        for (str in strings) {
            jsonArray.put(str)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toStringList(jsonString: String?): List<String> {
        if (jsonString.isNullOrBlank() || jsonString == "[]") return emptyList()
        val list = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {
            // Return empty list on parse error
        }
        return list
    }
}
