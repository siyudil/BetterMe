package com.sliit.betterme.ui.habits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.sliit.betterme.R

class OverallHabitsAdapter : RecyclerView.Adapter<OverallHabitsAdapter.Holder>() {
    private val items = mutableListOf<Habit>()

    fun submit(list: List<Habit>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_overall_habit, parent, false)
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
        private val grid: GridLayout = v.findViewById(R.id.gridDots)
        fun bind(h: Habit) {
            tvIcon.text = h.icon
            tvTitle.text = h.title
            tvFreq.text = "Everyday"
            grid.removeAllViews()
            grid.columnCount = 12
            grid.rowCount = 7
            val ctx = itemView.context
            val size = (ctx.resources.displayMetrics.density * 8).toInt()
            val pad = (ctx.resources.displayMetrics.density * 3).toInt()
            val total = grid.columnCount * grid.rowCount
            repeat(total) { idx ->
                val dot = View(ctx)
                val lp = GridLayout.LayoutParams()
                lp.width = size
                lp.height = size
                lp.setMargins(pad, pad, pad, pad)
                dot.layoutParams = lp
                val filled = (idx % 5 == 0) // placeholder pattern
                dot.setBackgroundResource(if (filled) R.drawable.overall_dot_filled else R.drawable.overall_dot_empty)
                grid.addView(dot)
            }
        }
    }
}
