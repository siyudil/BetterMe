package com.sliit.betterme.ui.habits

enum class TimeOfDay { MORNING, AFTERNOON, EVENING, NIGHT }

data class Habit(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    var colorHex: String = "#FFE0E0",
    var icon: String = "\uD83D\uDE0A", // emoji fallback
    var timeOfDay: TimeOfDay = TimeOfDay.MORNING,
    var completed: Boolean = false,
    var doneDates: Set<String> = emptySet() // yyyy-MM-dd keys
)
