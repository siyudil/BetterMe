package com.sliit.betterme.ui.habits

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.sliit.betterme.R

class HabitsAdapter(
    private val onToggle: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit,
    private val onEdit: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.Holder>() {

    private val items = mutableListOf<Habit>()
    val currentList: List<Habit> get() = items

    fun submit(list: List<Habit>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.bind(item, onToggle, onEdit)
    }

    override fun getItemCount(): Int = items.size

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        private val card: MaterialCardView = v as MaterialCardView
        private val tvIcon: TextView = v.findViewById(R.id.tvIcon)
        private val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        private val imgCompleted: ImageView = v.findViewById(R.id.imgCompleted)
        fun bind(h: Habit, onToggle: (Habit) -> Unit, onEdit: (Habit) -> Unit) {
            tvIcon.text = h.icon
            tvTitle.text = h.title
            try { card.setCardBackgroundColor(Color.parseColor(h.colorHex)) } catch (_: Exception) {}
            if (h.completed) {
                imgCompleted.visibility = View.VISIBLE
                imgCompleted.setImageResource(android.R.drawable.checkbox_on_background)
                tvIcon.background = itemView.context.getDrawable(R.drawable.bg_check_green)
                tvIcon.text = "✔"
            } else {
                imgCompleted.visibility = View.GONE
                tvIcon.background = null
                tvIcon.text = h.icon
            }
            card.setOnClickListener { onToggle(h) }
            card.setOnLongClickListener { onEdit(h); true }
        }
    }
}
