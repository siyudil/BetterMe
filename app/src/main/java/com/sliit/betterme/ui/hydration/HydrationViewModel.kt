package com.sliit.betterme.ui.hydration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sliit.betterme.data.Prefs
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class HydrationViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = Prefs(app)

    private val _goal = MutableLiveData(2500)
    val goal: LiveData<Int> = _goal

    private val _totalToday = MutableLiveData(0)
    val totalToday: LiveData<Int> = _totalToday

    private val _entriesToday = MutableLiveData<List<HydrationEntry>>(emptyList())
    val entriesToday: LiveData<List<HydrationEntry>> = _entriesToday

    private val sdfYMD = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init { refreshToday() }

    fun setGoal(value: Int){ _goal.value = value; refreshToday() }

    fun refreshToday() {
        val all = loadAll()
        val todayKey = sdfYMD.format(Date())
        val today = all.filter { it.dateKey == todayKey }
        _entriesToday.value = today.sortedByDescending { it.timeMillis }
        _totalToday.value = today.sumOf { it.amountMl }
    }

    fun addEntry(name: String, type: String, amount: Int, timeMillis: Long, reminder: Boolean) : HydrationEntry {
        val entry = HydrationEntry(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            amountMl = amount,
            timeMillis = timeMillis,
            dateKey = sdfYMD.format(Date(timeMillis)),
            reminder = reminder
        )
        val arr = prefs.getJsonArray(KEYS)
        arr.put(entry.toJson())
        prefs.putJsonArray(KEYS, arr)
        refreshToday()
        return entry
    }

    fun getAllEntries(): List<HydrationEntry> {
        return loadAll()
    }

    fun deleteEntry(id: String){
        val arr = prefs.getJsonArray(KEYS)
        val out = JSONArray()
        for (i in 0 until arr.length()){
            val o = arr.optJSONObject(i) ?: continue
            if (o.optString("id") != id) out.put(o)
        }
        prefs.putJsonArray(KEYS, out)
        refreshToday()
    }

    fun updateEntry(id: String, amount: Int, timeMillis: Long){
        val arr = prefs.getJsonArray(KEYS)
        for (i in 0 until arr.length()){
            val o = arr.optJSONObject(i) ?: continue
            if (o.optString("id") == id){
                o.put("amount", amount)
                o.put("time", timeMillis)
                o.put("dateKey", sdfYMD.format(Date(timeMillis)))
                break
            }
        }
        prefs.putJsonArray(KEYS, arr)
        refreshToday()
    }

    private fun loadAll(): List<HydrationEntry> {
        val arr: JSONArray = prefs.getJsonArray(KEYS)
        val list = mutableListOf<HydrationEntry>()
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            list.add(HydrationEntry.fromJson(o))
        }
        return list
    }

    companion object {
        private const val KEYS = "hydration_entries"
    }
}

data class HydrationEntry(
    val id: String,
    val name: String,
    val type: String, // "water" or "drink"
    val amountMl: Int,
    val timeMillis: Long,
    val dateKey: String,
    val reminder: Boolean
){
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("type", type)
        put("amount", amountMl)
        put("time", timeMillis)
        put("dateKey", dateKey)
        put("reminder", reminder)
    }
    companion object {
        fun fromJson(o: JSONObject) = HydrationEntry(
            id = o.optString("id"),
            name = o.optString("name"),
            type = o.optString("type"),
            amountMl = o.optInt("amount"),
            timeMillis = o.optLong("time"),
            dateKey = o.optString("dateKey"),
            reminder = o.optBoolean("reminder", false)
        )
    }
}
