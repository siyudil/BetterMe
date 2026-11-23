package com.sliit.betterme.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sliit.betterme.R
import com.sliit.betterme.ui.views.MoodBarsView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodReportFragment : Fragment() {
    private val viewModel: MoodViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mood_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bars = view.findViewById<MoodBarsView>(R.id.moodBars)
        val fmtDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fmtHour = SimpleDateFormat("H", Locale.getDefault())
        val fmtLabel = SimpleDateFormat("HH:mm", Locale.getDefault())

        viewModel.moods.observe(viewLifecycleOwner) { list ->
            val todayKey = fmtDay.format(Date())
            val today = list.filter { fmtDay.format(Date(it.timestamp)) == todayKey }.sortedBy { it.timestamp }
            val hours = today.map { fmtHour.format(Date(it.timestamp)).toFloat() }
            val levels = today.map { emojiToLevel(it.emoji).toFloat() }
            val labels = today.map { fmtLabel.format(Date(it.timestamp)) }
            val colors = IntArray(today.size) { idx -> safeParseColor(today[idx].colorHex) }
            bars.setData(hours, levels, labels, colors)
        }
    }

    private fun emojiToLevel(emoji: String): Int = when (emoji) {
        "😎" -> 5
        "😀" -> 4
        "🙂" -> 3
        "😐" -> 3
        "😕" -> 2
        "😢" -> 1
        "😡" -> 1
        else -> 3
    }

    private fun safeParseColor(hex: String): Int = try {
        android.graphics.Color.parseColor(hex)
    } catch (_: Exception) {
        android.graphics.Color.parseColor("#B39DDB")
    }
}
