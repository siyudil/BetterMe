package com.sliit.betterme.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("betterme_prefs", Context.MODE_PRIVATE)

    fun getString(key: String, def: String = ""): String = sp.getString(key, def) ?: def
    fun putString(key: String, value: String) { sp.edit().putString(key, value).apply() }

    fun getJsonArray(key: String): JSONArray {
        val text = getString(key, "[]")
        return try { JSONArray(text) } catch (_: Exception) { JSONArray() }
    }
    fun putJsonArray(key: String, arr: JSONArray) { putString(key, arr.toString()) }

    companion object Keys {
        const val HABITS = "habits_json"
        const val MOODS = "moods_json"
        const val SETTINGS = "settings_json"
    }
}
