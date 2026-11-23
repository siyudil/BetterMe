package com.sliit.betterme.ui.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.sliit.betterme.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodAdapter(
    private val items: MutableList<MoodEntry>,
    private val onEdit: (MoodEntry) -> Unit,
    private val onDelete: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.VH>() {
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val card = holder.itemView as? MaterialCardView
        try { card?.setCardBackgroundColor(android.graphics.Color.parseColor(item.colorHex)) } catch (_: Exception) {}
        holder.tvEmoji.text = item.emoji
        val feelingsText = if (item.feelings.isNotEmpty()) item.feelings.joinToString(", ") else ""
        holder.tvNote.text = listOf(item.note, feelingsText).filter { it.isNotBlank() }.joinToString(" • ")
        holder.tvTime.text = SimpleDateFormat("EEE, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
        holder.itemView.setOnClickListener { onEdit(item) }
        holder.itemView.setOnLongClickListener { onDelete(item); true }
    }

    fun update(newItems: List<MoodEntry>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
