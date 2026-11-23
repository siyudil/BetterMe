package com.sliit.betterme.ui.hydration

import android.graphics.Color
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sliit.betterme.R
import com.sliit.betterme.ui.hydration.views.BarChartView
import com.sliit.betterme.ui.hydration.views.DonutChartView
import com.sliit.betterme.ui.hydration.views.LineChartView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReportsHydrationFragment : Fragment(R.layout.fragment_hydration_reports) {
    private val vm: HydrationViewModel by activityViewModels()

    private enum class Mode { TODAY, WEEK }
    private var mode = Mode.WEEK
    private var endDate: Date = Calendar.getInstance().time

    private val sdfMonthDay = SimpleDateFormat("MMM d", Locale.getDefault())
    private val sdfRangeYear = SimpleDateFormat(
        "MMM d", Locale.getDefault()
    )
    private val sdfYMD = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_hydration_reports, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnToday = view.findViewById<Button>(R.id.btnToday)
        val btnWeekly = view.findViewById<Button>(R.id.btnWeekly)
        val btnPrev = view.findViewById<ImageView>(R.id.btnPrev)
        val btnNext = view.findViewById<ImageView>(R.id.btnNext)

        btnToday.setOnClickListener { mode = Mode.TODAY; endDate = Calendar.getInstance().time; render(view) }
        btnWeekly.setOnClickListener { mode = Mode.WEEK; endDate = Calendar.getInstance().time; render(view) }
        btnPrev.setOnClickListener { shiftRange(-1); render(view) }
        btnNext.setOnClickListener { if (canNext()) { shiftRange(+1); render(view) } }

        render(view)
    }

    private fun shiftRange(step: Int){
        val cal = Calendar.getInstance(); cal.time = endDate
        if (mode == Mode.TODAY){
            cal.add(Calendar.DAY_OF_YEAR, step)
        } else {
            cal.add(Calendar.DAY_OF_YEAR, 7 * step)
        }
        endDate = cal.time
    }

    private fun canNext(): Boolean {
        val today = Calendar.getInstance()
        val target = Calendar.getInstance(); target.time = endDate
        return if (mode == Mode.TODAY){
            // Allow moving forward only if endDate is strictly before today
            target.get(Calendar.YEAR) < today.get(Calendar.YEAR) ||
            target.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR)
        } else {
            // For week view, week ends at endDate; disallow if endDate is today or after
            target.get(Calendar.YEAR) < today.get(Calendar.YEAR) ||
            target.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR)
        }
    }

    private fun sameDay(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    private fun render(v: View){
        v.findViewById<TextView>(R.id.tvRange).text = buildRangeLabel()
        styleTabs(v)
        v.findViewById<ImageView>(R.id.btnNext).alpha = if (canNext()) 1f else 0.3f

        val completion = v.findViewById<BarChartView>(R.id.chartCompletion)
        val hydrate = v.findViewById<LineChartView>(R.id.chartHydrate)
        val types = v.findViewById<DonutChartView>(R.id.chartTypes)
        val legend = v.findViewById<LinearLayout>(R.id.legendTypes)

        if (mode == Mode.TODAY){
            val dayKey = sdfYMD.format(endDate)
            val entries = vm.getAllEntries().filter { it.dateKey == dayKey }
            val goal = vm.goal.value ?: 0
            val total = entries.sumOf { it.amountMl }
            // Bar chart: single bar for today completion %
            val percent = if (goal == 0) 0f else (total.toFloat() / goal.toFloat() * 100f).coerceAtMost(100f)
            completion.setData(listOf(percent), listOf("Today"))
            // Line chart: cumulative throughout day at 6 time buckets
            val buckets = buildDayBuckets(entries)
            hydrate.setData(buckets.map { it.first.toFloat() }, buckets.map { it.second })
            // Types
            val slices = buildTypeSlices(entries)
            types.setData(slices, centerPercent(total, goal))
            buildLegend(legend, slices)
        } else {
            // Weekly mode: last 7 days ending at endDate
            val days = last7Days(endDate)
            val goal = vm.goal.value ?: 0
            val values = mutableListOf<Float>()
            val labels = mutableListOf<String>()
            val lineValues = mutableListOf<Float>()
            val lineLabels = mutableListOf<String>()
            val all = vm.getAllEntries()
            var totalWeek = 0
            val typeAgg = mutableListOf<HydrationEntry>()
            for (d in days){
                val key = sdfYMD.format(d)
                val list = all.filter { it.dateKey == key }
                val total = list.sumOf { it.amountMl }
                totalWeek += total
                typeAgg.addAll(list)
                val pct = if (goal == 0) 0f else (total.toFloat() / goal.toFloat() * 100f).coerceAtMost(100f)
                values.add(pct)
                labels.add(SimpleDateFormat("d", Locale.getDefault()).format(d))
                lineValues.add(total.toFloat())
                lineLabels.add(SimpleDateFormat("d", Locale.getDefault()).format(d))
            }
            completion.setData(values, labels)
            hydrate.setData(lineValues, lineLabels)
            val slices = buildTypeSlices(typeAgg)
            types.setData(slices, centerPercent(totalWeek, goal * 7))
            buildLegend(legend, slices)
        }
    }

    private fun styleTabs(v: View){
        val btnToday = v.findViewById<Button>(R.id.btnToday)
        val btnWeekly = v.findViewById<Button>(R.id.btnWeekly)
        val primary = Color.parseColor("#1E90FF")
        val inactive = Color.parseColor("#EEEEEE")
        if (mode == Mode.TODAY){
            btnToday.backgroundTintList = ColorStateList.valueOf(primary)
            btnToday.setTextColor(Color.WHITE)
            btnWeekly.backgroundTintList = ColorStateList.valueOf(inactive)
            btnWeekly.setTextColor(Color.BLACK)
        } else {
            btnWeekly.backgroundTintList = ColorStateList.valueOf(primary)
            btnWeekly.setTextColor(Color.WHITE)
            btnToday.backgroundTintList = ColorStateList.valueOf(inactive)
            btnToday.setTextColor(Color.BLACK)
        }
    }

    private fun buildRangeLabel(): String {
        return if (mode == Mode.TODAY){
            SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(endDate)
        } else {
            val calEnd = Calendar.getInstance(); calEnd.time = endDate
            val calStart = Calendar.getInstance(); calStart.time = endDate; calStart.add(Calendar.DAY_OF_YEAR, -6)
            val showYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(endDate)
            "${sdfMonthDay.format(calStart.time)} - ${sdfMonthDay.format(calEnd.time)}, ${showYear}"
        }
    }

    private fun last7Days(end: Date): List<Date> {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance(); cal.time = end
        for (i in 6 downTo 0){
            val c = Calendar.getInstance(); c.time = end; c.add(Calendar.DAY_OF_YEAR, -i)
            list.add(c.time)
        }
        return list
    }

    private fun buildDayBuckets(entries: List<HydrationEntry>): List<Pair<Int,String>>{
        // 6 buckets: 6am, 10am, 2pm, 6pm, 10pm, 12am
        val points = listOf(6,10,14,18,22,24)
        val sums = IntArray(points.size)
        val labels = arrayOf("6a","10a","2p","6p","10p","12a")
        val cal = Calendar.getInstance()
        for (e in entries){
            cal.timeInMillis = e.timeMillis
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            var idx = points.indexOfFirst { hour < it }
            if (idx < 0) idx = points.lastIndex
            sums[idx] += e.amountMl
        }
        return sums.mapIndexed { i, v -> v to labels[i] }
    }

    private fun buildTypeSlices(entries: List<HydrationEntry>): List<DonutChartView.Slice>{
        val by = entries.groupBy { if (it.type.equals("water", true)) "Water" else it.name }
        val colors = listOf(
            Color.parseColor("#FF8C00"), // orange
            Color.parseColor("#1E90FF"), // blue
            Color.parseColor("#32CD32"), // green
            Color.parseColor("#FF1493"), // pink
            Color.parseColor("#9400D3"), // purple
            Color.parseColor("#8B4513")  // brown
        )
        val slices = mutableListOf<DonutChartView.Slice>()
        var i = 0
        for ((k, list) in by){
            val total = list.sumOf { it.amountMl }.toFloat()
            slices.add(DonutChartView.Slice(total, colors[i % colors.size], k))
            i++
        }
        if (slices.isEmpty()) slices.add(DonutChartView.Slice(1f, Color.parseColor("#EEEEEE"), "None"))
        return slices
    }

    private fun buildLegend(container: LinearLayout, slices: List<DonutChartView.Slice>){
        container.removeAllViews()
        val ctx = container.context
        for (s in slices){
            val row = LinearLayout(ctx)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(0, 6, 0, 6)
            val dot = View(ctx)
            val size = (12 * ctx.resources.displayMetrics.density).toInt()
            val lpDot = LinearLayout.LayoutParams(size, size)
            lpDot.rightMargin = (8 * ctx.resources.displayMetrics.density).toInt()
            dot.layoutParams = lpDot
            dot.background = ctx.getDrawable(R.drawable.shape_circle)
            dot.backgroundTintList = ColorStateList.valueOf(s.color)
            val tv = TextView(ctx)
            tv.text = "${s.label} ${s.value.toInt()} mL"
            row.addView(dot)
            row.addView(tv)
            container.addView(row)
        }
    }

    private fun centerPercent(total: Int, goal: Int): String {
        if (goal <= 0) return "0%"
        val pct = (total.toFloat() / goal.toFloat() * 100f).coerceAtMost(999f)
        return String.format(Locale.getDefault(), "%.0f%%", pct)
    }
}
