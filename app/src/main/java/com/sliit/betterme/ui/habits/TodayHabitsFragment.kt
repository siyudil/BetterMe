package com.sliit.betterme.ui.habits

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.sliit.betterme.R

class TodayHabitsFragment : Fragment() {
    private val viewModel: HabitsViewModel by activityViewModels()
    private lateinit var adapter: HabitsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_today_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = HabitsAdapter(
            onToggle = { viewModel.toggleComplete(it.id) },
            onDelete = { viewModel.deleteHabit(it.id) },
            onEdit = { h ->
                val sheet = AddHabitBottomSheet()
                sheet.arguments = android.os.Bundle().apply {
                    putLong("edit_id", h.id)
                    putString("edit_title", h.title)
                    putString("edit_icon", h.icon)
                    putString("edit_color", h.colorHex)
                    putString("edit_tod", h.timeOfDay.name)
                }
                sheet.show(childFragmentManager, "edit_habit")
            }
        )
        recycler.adapter = adapter

        // Swipe: left mark complete, right delete
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val habit = adapter.currentList[vh.bindingAdapterPosition]
                if (dir == ItemTouchHelper.LEFT) {
                    viewModel.toggleComplete(habit.id)
                } else {
                    viewModel.deleteHabit(habit.id)
                    Snackbar.make(view, "Deleted", Snackbar.LENGTH_SHORT).setAction("Undo") {
                        viewModel.addHabit(habit)
                    }.show()
                }
            }
            override fun onChildDraw(c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = vh.itemView
                val height = itemView.bottom - itemView.top
                val corner = 24f
                val dp16 = itemView.resources.displayMetrics.density * 16
                val pillWidth = (itemView.width * 0.35f).coerceAtMost(itemView.resources.displayMetrics.density * 180)

                // Paints
                val red = android.graphics.Paint().apply { isAntiAlias = true; color = Color.parseColor("#FF5A5F") }
                val green = android.graphics.Paint().apply { isAntiAlias = true; color = Color.parseColor("#2ECC71") }
                val textPaint = android.graphics.Paint().apply {
                    color = Color.WHITE
                    isAntiAlias = true
                    textSize = 36f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                // Do NOT change itemView background; draw compact pill overlay so card color remains visible
                if (dX > 0f) {
                    // Right swipe -> DELETE (draw pill near left edge)
                    val rect = android.graphics.RectF(
                        itemView.left + dp16,
                        itemView.top + dp16,
                        itemView.left + dp16 + pillWidth,
                        itemView.bottom - dp16
                    )
                    c.drawRoundRect(rect, corner, corner, red)
                    val cx = rect.centerX()
                    val cy = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
                    c.drawText("Delete →", cx, cy, textPaint)
                } else if (dX < 0f) {
                    // Left swipe -> DONE (draw pill near right edge)
                    val rect = android.graphics.RectF(
                        itemView.right - dp16 - pillWidth,
                        itemView.top + dp16,
                        itemView.right - dp16,
                        itemView.bottom - dp16
                    )
                    c.drawRoundRect(rect, corner, corner, green)
                    val cx = rect.centerX()
                    val cy = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
                    c.drawText("Done", cx, cy, textPaint)
                }

                // translate item with swipe
                super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive)
            }
            override fun clearView(rv: RecyclerView, vh: RecyclerView.ViewHolder) {
                super.clearView(rv, vh)
                vh.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        })
        helper.attachToRecyclerView(recycler)

        // Chips filter
        fun filterFn(list: List<Habit>): List<Habit> {
            val chipAll = view.findViewById<Chip>(R.id.chipAll)
            val chipMorning = view.findViewById<Chip>(R.id.chipMorning)
            val chipAfternoon = view.findViewById<Chip>(R.id.chipAfternoon)
            val chipEvening = view.findViewById<Chip>(R.id.chipEvening)
            val chipNight = view.findViewById<Chip>(R.id.chipNight)
            return when {
                chipMorning.isChecked -> list.filter { it.timeOfDay == TimeOfDay.MORNING }
                chipAfternoon.isChecked -> list.filter { it.timeOfDay == TimeOfDay.AFTERNOON }
                chipEvening.isChecked -> list.filter { it.timeOfDay == TimeOfDay.EVENING }
                chipNight.isChecked -> list.filter { it.timeOfDay == TimeOfDay.NIGHT }
                else -> list
            }
        }

        fun update(list: List<Habit>) {
            adapter.submit(list)
        }

        viewModel.habits.observe(viewLifecycleOwner) { list -> update(filterFn(list)) }
        listOf(R.id.chipAll, R.id.chipMorning, R.id.chipAfternoon, R.id.chipEvening, R.id.chipNight)
            .forEach { id -> view.findViewById<Chip>(id).setOnCheckedChangeListener { _, _ ->
                update(filterFn(viewModel.habits.value.orEmpty()))
            } }

        // Add
        view.findViewById<View>(R.id.fabAdd).setOnClickListener {
            AddHabitBottomSheet().show(childFragmentManager, "add_habit")
        }

        // Switch to the category of the newly added habit
        parentFragmentManager.setFragmentResultListener("new_habit_tod", viewLifecycleOwner) { _, bundle ->
            when (bundle.getString("tod")) {
                TimeOfDay.MORNING.name -> view.findViewById<Chip>(R.id.chipMorning).isChecked = true
                TimeOfDay.AFTERNOON.name -> view.findViewById<Chip>(R.id.chipAfternoon).isChecked = true
                TimeOfDay.EVENING.name -> view.findViewById<Chip>(R.id.chipEvening).isChecked = true
                TimeOfDay.NIGHT.name -> view.findViewById<Chip>(R.id.chipNight).isChecked = true
                else -> view.findViewById<Chip>(R.id.chipAll).isChecked = true
            }
        }
    }
}
