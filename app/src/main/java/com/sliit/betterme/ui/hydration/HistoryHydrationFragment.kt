package com.sliit.betterme.ui.hydration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sliit.betterme.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryHydrationFragment : Fragment(R.layout.fragment_hydration_history) {
    private val vm: HydrationViewModel by activityViewModels()
    private var selectedEndDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_hydration_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvWeek = view.findViewById<RecyclerView>(R.id.rvWeek)
        val rvGrouped = view.findViewById<RecyclerView>(R.id.rvGrouped)
        val tvEmpty = view.findViewById<View>(R.id.tvEmpty)

        val weekAdapter = WeekAdapter { selectedDate ->
            selectedEndDate = selectedDate
            bindWeekAndList(selectedDate, rvGrouped, tvEmpty)
        }
        rvWeek.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvWeek.adapter = weekAdapter

        val today = Calendar.getInstance().time
        weekAdapter.submit(buildWeekEnding(today), today)
        selectedEndDate = today
        bindWeekAndList(today, rvGrouped, tvEmpty)
    }

    private fun bindWeekAndList(endDate: Date, rvGrouped: RecyclerView, tvEmpty: View){
        val items = buildGroupedItemsForWeek(endDate)
        tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        if (rvGrouped.adapter == null){
            rvGrouped.layoutManager = LinearLayoutManager(requireContext())
            rvGrouped.adapter = GroupedHistoryAdapter { entry ->
                // Show popup or confirm then delete
                vm.deleteEntry(entry.id)
                selectedEndDate?.let { end ->
                    (rvGrouped.adapter as GroupedHistoryAdapter).submit(buildGroupedItemsForWeek(end))
                }
            }
        }
        (rvGrouped.adapter as GroupedHistoryAdapter).submit(items)
    }

    private fun buildWeekEnding(date: Date): List<DayChip>{
        val cal = Calendar.getInstance()
        cal.time = date
        val list = mutableListOf<DayChip>()
        for (i in 6 downTo 0){
            val c = Calendar.getInstance()
            c.time = date
            c.add(Calendar.DAY_OF_YEAR, -i)
            list.add(DayChip(c.time))
        }
        return list
    }

    private val sdfYMD = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val sdfHeader = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

    private fun buildGroupedItemsForWeek(endDate: Date): List<ListItem>{
        val calEnd = Calendar.getInstance()
        calEnd.time = endDate
        val calStart = Calendar.getInstance()
        calStart.time = endDate
        calStart.add(Calendar.DAY_OF_YEAR, -6)

        val all = vm.getAllEntries()
        val inRange = all.filter {
            val d = sdfYMD.parse(it.dateKey) ?: Date(it.timeMillis)
            d >= stripTime(calStart).time && d <= endOfDay(calEnd).time
        }.sortedByDescending { it.timeMillis }

        val todayKey = sdfYMD.format(Date())
        val yesterdayCal = Calendar.getInstance()
        yesterdayCal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayKey = sdfYMD.format(yesterdayCal.time)

        val byDate = inRange.groupBy { it.dateKey }
        val keys = mutableListOf<String>()
        val cIter = Calendar.getInstance()
        cIter.time = calEnd.time
        for (i in 0..6){
            val key = sdfYMD.format(cIter.time)
            if (byDate.containsKey(key)) keys.add(key)
            cIter.add(Calendar.DAY_OF_YEAR, -1)
        }

        val items = mutableListOf<ListItem>()
        for (k in keys){
            val title = when(k){
                todayKey -> "Today, " + sdfHeader.format(sdfYMD.parse(k)!!)
                yesterdayKey -> "Yesterday, " + sdfHeader.format(sdfYMD.parse(k)!!)
                else -> sdfHeader.format(sdfYMD.parse(k)!!)
            }
            items.add(ListItem.Header(title))
            byDate[k]?.sortedByDescending { it.timeMillis }?.forEach { items.add(ListItem.Entry(it)) }
        }
        return items
    }

    private fun stripTime(c: Calendar): Calendar {
        val x = c.clone() as Calendar
        x.set(Calendar.HOUR_OF_DAY, 0)
        x.set(Calendar.MINUTE, 0)
        x.set(Calendar.SECOND, 0)
        x.set(Calendar.MILLISECOND, 0)
        return x
    }
    private fun endOfDay(c: Calendar): Calendar {
        val x = c.clone() as Calendar
        x.set(Calendar.HOUR_OF_DAY, 23)
        x.set(Calendar.MINUTE, 59)
        x.set(Calendar.SECOND, 59)
        x.set(Calendar.MILLISECOND, 999)
        return x
    }
}

private data class DayChip(val date: Date){
    val dayName: String get() = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
    val dayNum: String get() = SimpleDateFormat("d", Locale.getDefault()).format(date)
}

private class WeekAdapter(val onSelect: (Date)->Unit): RecyclerView.Adapter<WeekDayVH>(){
    private val items = mutableListOf<DayChip>()
    private var selected: String? = null
    private val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    fun submit(list: List<DayChip>, selectedDate: Date){
        items.clear()
        items.addAll(list)
        selected = sdf.format(selectedDate)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekDayVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_week_day, parent, false)
        return WeekDayVH(v)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: WeekDayVH, position: Int) {
        val item = items[position]
        val isSel = sdf.format(item.date) == selected
        holder.bind(item, isSel)
        holder.itemView.setOnClickListener {
            selected = sdf.format(item.date)
            notifyDataSetChanged()
            onSelect(item.date)
        }
    }
}

private class WeekDayVH(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(day: DayChip, selected: Boolean){
        val tvTop = itemView.findViewById<TextView>(R.id.tvDay)
        val tvNum = itemView.findViewById<TextView>(R.id.tvNum)
        val circle = itemView.findViewById<View>(R.id.circle)
        tvTop.text = day.dayName
        tvNum.text = day.dayNum
        circle.setBackgroundResource(if (selected) R.drawable.weekly_dot_checked else R.drawable.weekly_dot_empty)
    }
}

private sealed class ListItem{
    data class Header(val title: String): ListItem()
    data class Entry(val e: HydrationEntry): ListItem()
}

private class GroupedHistoryAdapter(val onDelete: (HydrationEntry)->Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val items = mutableListOf<ListItem>()
    fun submit(list: List<ListItem>){ items.clear(); items.addAll(list); notifyDataSetChanged() }
    override fun getItemCount(): Int = items.size
    override fun getItemViewType(position: Int): Int = when(items[position]){
        is ListItem.Header -> 0
        is ListItem.Entry -> 1
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == 0){
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_section_header, parent, false)
            object: RecyclerView.ViewHolder(v){}
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_hydration_history, parent, false)
            HydrationHistoryVH(v)
        }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = items[position]){
            is ListItem.Header -> {
                (holder.itemView.findViewById<TextView>(R.id.tvHeader)).text = item.title
            }
            is ListItem.Entry -> {
                (holder as HydrationHistoryVH).bind(item.e)
                val btn = holder.itemView.findViewById<ImageView>(R.id.btnMenu)
                btn?.setOnClickListener { v ->
                    val popup = PopupMenu(v.context, v)
                    popup.menu.add(0, 1, 0, "Delete")
                    popup.setOnMenuItemClickListener { mi ->
                        if (mi.itemId == 1) onDelete(item.e)
                        true
                    }
                    popup.show()
                }
            }
        }
    }
}
