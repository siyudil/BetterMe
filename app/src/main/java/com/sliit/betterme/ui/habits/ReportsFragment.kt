package com.sliit.betterme.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sliit.betterme.R
import com.sliit.betterme.ui.views.BarChartView
import com.sliit.betterme.ui.views.LineChartView

class ReportsFragment : Fragment() {
    private val viewModel: HabitsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvStreak = view.findViewById<TextView>(R.id.tvStreak)
        val tvRate = view.findViewById<TextView>(R.id.tvCompletionRate)
        val bar = view.findViewById<BarChartView>(R.id.barChart)
        val line = view.findViewById<LineChartView>(R.id.lineChart)

        viewModel.habits.observe(viewLifecycleOwner) { list ->
            val total = list.size
            val completedNow = list.count { it.completed }
            val rate = if (total == 0) 0 else (completedNow * 100 / total)

            // Simple streak approximation: consecutive days ending today with at least one completion
            val todayKey = todayKey()
            val last7 = lastNDaysKeys(7)
            var streak = 0
            for (k in last7) {
                val anyDone = list.any { it.doneDates.contains(k) }
                if (anyDone) streak++ else if (k == todayKey) break else break
            }
            tvStreak.text = "$streak days"
            tvRate.text = "$rate%"

            // Bar chart: each bar = one habit today (1 if completed today, else 0), colored by habit.colorHex
            val barValues = list.map { h -> if (h.doneDates.contains(todayKey)) 1f else 0f }
            val barColors = IntArray(list.size) { idx ->
                safeParseColor(list[idx].colorHex)
            }
            bar.setData(barValues, colors = barColors)

            // Line chart: last 7 days total completions (sum over habits per day)
            val weeklyCounts = last7.map { dayKey ->
                list.count { it.doneDates.contains(dayKey) }.toFloat()
            }
            line.setData(weeklyCounts)
        }
    }

    private fun todayKey(): String {
        val cal = java.util.Calendar.getInstance()
        val y = cal.get(java.util.Calendar.YEAR)
        val m = cal.get(java.util.Calendar.MONTH) + 1
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", y, m, d)
    }

    private fun lastNDaysKeys(n: Int): List<String> {
        val cal = java.util.Calendar.getInstance()
        val list = mutableListOf<String>()
        for (i in 0 until n) {
            val y = cal.get(java.util.Calendar.YEAR)
            val m = cal.get(java.util.Calendar.MONTH) + 1
            val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
            list.add(String.format("%04d-%02d-%02d", y, m, d))
            cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
        }
        list.reverse() // oldest to newest
        return list
    }

    private fun safeParseColor(hex: String): Int {
        return try { android.graphics.Color.parseColor(hex) } catch (_: Exception) { android.graphics.Color.parseColor("#B39DDB") }
    }
}
