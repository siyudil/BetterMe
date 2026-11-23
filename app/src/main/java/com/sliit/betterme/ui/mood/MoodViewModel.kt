package com.sliit.betterme.ui.mood

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sliit.betterme.data.Prefs
import org.json.JSONArray

class MoodViewModel(app: Application) : AndroidViewModel(app) {
    private val _moods = MutableLiveData<List<MoodEntry>>(emptyList())
    val moods: LiveData<List<MoodEntry>> = _moods

    private val _editing = MutableLiveData<MoodEntry?>(null)
    val editing: LiveData<MoodEntry?> = _editing

    private val prefs by lazy { Prefs(getApplication()) }

    fun load() {
        val arr: JSONArray = prefs.getJsonArray(Prefs.MOODS)
        val list = mutableListOf<MoodEntry>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            list.add(MoodEntry.fromJson(obj))
        }
        list.sortByDescending { it.timestamp }
        _moods.value = list
    }

    private fun persist() {
        val arr = JSONArray()
        _moods.value.orEmpty().forEach { arr.put(it.toJson()) }
        prefs.putJsonArray(Prefs.MOODS, arr)
    }

    fun addOrUpdate(entry: MoodEntry) {
        val cur = _moods.value.orEmpty().toMutableList()
        val idx = cur.indexOfFirst { it.id == entry.id }
        if (idx >= 0) cur[idx] = entry else cur.add(0, entry)
        _moods.value = cur
        persist()
    }

    fun delete(id: String) {
        _moods.value = _moods.value.orEmpty().filterNot { it.id == id }
        persist()
    }

    fun setEditing(entry: MoodEntry?) { _editing.value = entry }
}
