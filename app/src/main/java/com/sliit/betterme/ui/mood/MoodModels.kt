package com.sliit.betterme.ui.mood

import org.json.JSONArray
import org.json.JSONObject

data class MoodEntry(
    val id: String,
    val timestamp: Long,
    val emoji: String,
    val note: String,
    val feelings: List<String> = emptyList(),
    val colorHex: String = "#EDE7F6"
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("timestamp", timestamp)
        put("emoji", emoji)
        put("note", note)
        val arr = JSONArray()
        feelings.forEach { arr.put(it) }
        put("feelings", arr)
        put("colorHex", colorHex)
    }

    companion object {
        fun fromJson(obj: JSONObject): MoodEntry = MoodEntry(
            id = obj.optString("id"),
            timestamp = obj.optLong("timestamp"),
            emoji = obj.optString("emoji"),
            note = obj.optString("note"),
            feelings = (obj.optJSONArray("feelings") ?: JSONArray()).let { ja ->
                (0 until ja.length()).mapNotNull { i -> ja.optString(i, null) }
            },
            colorHex = obj.optString("colorHex", "#EDE7F6")
        )
    }
}
