package com.sliit.betterme.ui.habits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HabitsViewModel : ViewModel() {
    private val _habits = MutableLiveData<List<Habit>>(emptyList())
    val habits: LiveData<List<Habit>> = _habits

    fun addHabit(habit: Habit) {
        _habits.value = _habits.value.orEmpty() + habit
    }

    fun updateHabit(updated: Habit) {
        _habits.value = _habits.value.orEmpty().map {
            if (it.id == updated.id) {
                // Keep completion state if not explicitly set on updated
                val completed = updated.completed || it.completed
                it.copy(
                    title = updated.title,
                    colorHex = updated.colorHex,
                    icon = updated.icon,
                    timeOfDay = updated.timeOfDay,
                    completed = completed
                )
            } else it
        }
    }

    fun deleteHabit(id: Long) {
        _habits.value = _habits.value.orEmpty().filterNot { it.id == id }
    }

    private fun todayKey(): String {
        val cal = java.util.Calendar.getInstance()
        val y = cal.get(java.util.Calendar.YEAR)
        val m = cal.get(java.util.Calendar.MONTH) + 1
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", y, m, d)
    }

    fun toggleComplete(id: Long) {
        val today = todayKey()
        _habits.value = _habits.value.orEmpty().map { h ->
            if (h.id == id) {
                val hasToday = h.doneDates.contains(today)
                val newSet = if (hasToday) h.doneDates - today else h.doneDates + today
                h.copy(
                    completed = !hasToday,
                    doneDates = newSet
                )
            } else h
        }
    }
}
