package com.sliit.betterme.ui.mood

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sliit.betterme.R
import java.util.UUID

class MoodAddFragment : Fragment() {
    private val viewModel: MoodViewModel by activityViewModels()

    private val emojis = listOf(
        "😎","😀","🙂","😐","😕","😢","😡",
        "🤩","🥳","😴","🤗","😇","🤔","😭","😆","🥲"
    )
    private val feelingsOptions = listOf(
        "Happy","Brave","Motivated","Creative","Confident","Calm","Grateful","Peaceful","Excited","Loved","Hopeful","Inspired","Proud","Euphoric","Nostalgic"
    )
    // Same 8-color palette used in Add Habit
    private val colors = listOf("#FFD6E7", "#FFE6CC", "#FFF7B2", "#CFF8E6", "#CFEFF3", "#D6E9FF", "#E5D7FF", "#E8D6FF")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mood_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emojiContainer = view.findViewById<androidx.gridlayout.widget.GridLayout>(R.id.emojiContainer)
        val chipsFeelings = view.findViewById<ChipGroup>(R.id.chipsFeelings)
        val chipsColors = view.findViewById<ChipGroup>(R.id.chipsColors)
        val etNote = view.findViewById<EditText>(R.id.etNote)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveMood)
        val btnViewAll = view.findViewById<MaterialButton>(R.id.btnViewAllEmojis)

        var selectedEmoji = emojis[2]
        var selectedColor = colors[0]

        // Build emoji choices as grid cells
        fun addEmojiCell(emoji: String) {
            val tv = TextView(requireContext())
            tv.text = emoji
            tv.textSize = 28f
            tv.setPadding(16, 8, 16, 8)
            tv.setOnClickListener { selectedEmoji = emoji }
            val lp = androidx.gridlayout.widget.GridLayout.LayoutParams(
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            )
            val m = (8 * resources.displayMetrics.density).toInt()
            lp.setMargins(m, m, m, m)
            tv.layoutParams = lp
            emojiContainer.addView(tv)
        }

        fun renderEmojiRows(rows: Int) {
            val cols = if (emojiContainer.columnCount > 0) emojiContainer.columnCount else 6
            val maxCount = (rows.coerceIn(1, 2)) * cols
            emojiContainer.removeAllViews()
            emojis.take(maxCount).forEach { e -> addEmojiCell(e) }
        }
        // View all -> append more emojis or collapse
        val extraEmojis = listOf("🤩","🥳","😴","🤗","😇","🤔","😬","😭","🤯","😤","😓","😆","🥲","🤒","🤕")

        // default: two lines (expanded)
        run {
            val cols = if (emojiContainer.columnCount > 0) emojiContainer.columnCount else 6
            val need = 2 * cols
            val combined = if (emojis.size >= need) emojis else emojis + extraEmojis
            emojiContainer.removeAllViews()
            combined.take(need).forEach { e -> addEmojiCell(e) }
            btnViewAll.text = "View less"
            btnViewAll.setTag(R.id.btnViewAllEmojis, true)
        }
        btnViewAll.setOnClickListener {
            val expanded = (btnViewAll.getTag(R.id.btnViewAllEmojis) as? Boolean) == true
            if (!expanded) {
                // show two rows (use existing emoji list; ensure at least 2*cols by appending extras)
                val cols = if (emojiContainer.columnCount > 0) emojiContainer.columnCount else 6
                val need = 2 * cols
                val combined = if (emojis.size >= need) emojis else emojis + extraEmojis
                // re-render using first need items
                emojiContainer.removeAllViews()
                combined.take(need).forEach { e -> addEmojiCell(e) }
                btnViewAll.text = "View less"
                btnViewAll.setTag(R.id.btnViewAllEmojis, true)
            } else {
                // collapse to one row
                renderEmojiRows(1)
                btnViewAll.text = "View more"
                btnViewAll.setTag(R.id.btnViewAllEmojis, false)
            }
        }

        // Build feelings chips
        chipsFeelings.removeAllViews()
        feelingsOptions.forEach { f ->
            val chip = Chip(requireContext()).apply { text = f; isCheckable = true }
            chipsFeelings.addView(chip)
        }

        // Build color chips
        chipsColors.removeAllViews()
        colors.forEachIndexed { idx, hex ->
            val chip = Chip(requireContext()).apply {
                text = ""
                isCheckable = true
                setEnsureMinTouchTargetSize(false)
                try { setChipBackgroundColor(ColorStateList.valueOf(android.graphics.Color.parseColor(hex))) } catch (_: Exception) {}
                setPadding(24, 12, 24, 12)
            }
            chip.setOnClickListener { selectedColor = hex }
            chipsColors.addView(chip)
            if (idx == 0) chip.isChecked = true
        }

        // Prefill if editing
        viewModel.editing.observe(viewLifecycleOwner) { edit ->
            if (edit != null) {
                selectedEmoji = edit.emoji
                selectedColor = edit.colorHex
                etNote.setText(edit.note)
                for (i in 0 until chipsFeelings.childCount) {
                    val c = chipsFeelings.getChildAt(i) as Chip
                    c.isChecked = edit.feelings.contains(c.text.toString())
                }
            }
        }

        btnSave.setOnClickListener {
            val feelings = mutableListOf<String>()
            for (i in 0 until chipsFeelings.childCount) {
                val chip = chipsFeelings.getChildAt(i) as Chip
                if (chip.isChecked) feelings.add(chip.text.toString())
            }
            val editing = viewModel.editing.value
            val entry = if (editing != null) {
                editing.copy(
                    emoji = selectedEmoji,
                    note = etNote.text.toString(),
                    feelings = feelings,
                    colorHex = selectedColor,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                MoodEntry(
                    id = UUID.randomUUID().toString(),
                    timestamp = System.currentTimeMillis(),
                    emoji = selectedEmoji,
                    note = etNote.text.toString(),
                    feelings = feelings,
                    colorHex = selectedColor
                )
            }
            viewModel.addOrUpdate(entry)
            viewModel.setEditing(null)
            parentFragmentManager.setFragmentResult("mood_saved", bundleOf())
        }
    }
}
