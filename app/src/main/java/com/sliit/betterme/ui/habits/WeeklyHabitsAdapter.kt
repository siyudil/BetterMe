package com.sliit.betterme.ui.habits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sliit.betterme.R

class WeeklyHabitsAdapter : RecyclerView.Adapter<WeeklyHabitsAdapter.Holder>() {
    private val items = mutableListOf<Habit>()

    fun submit(list: List<Habit>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_weekly_habit, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        private val tvIcon: TextView = v.findViewById(R.id.tvIcon)
        private val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        private val tvFreq: TextView = v.findViewById(R.id.tvFreq)
        private val dots = listOf(
            v.findViewById<ImageView>(R.id.dotMon),
            v.findViewById<ImageView>(R.id.dotTue),
            v.findViewById<ImageView>(R.id.dotWed),
            v.findViewById<ImageView>(R.id.dotThu),
            v.findViewById<ImageView>(R.id.dotFri),
            v.findViewById<ImageView>(R.id.dotSat),
            v.findViewById<ImageView>(R.id.dotSun),
        )
        fun bind(h: Habit) {
            tvIcon.text = h.icon
            tvTitle.text = h.title
            // Placeholder frequency text; can be computed later
            tvFreq.text = "Everyday"
            // For now, mark Mon-Fri as checked if completed, else empty
            val checked = if (h.completed) setOf(0,1,2,3,4,5,6) else emptySet()
            dots.forEachIndexed { index, imageView ->
                imageView.setImageResource(if (checked.contains(index)) R.drawable.weekly_dot_checked else R.drawable.weekly_dot_empty)
            }
        }
    }
}
