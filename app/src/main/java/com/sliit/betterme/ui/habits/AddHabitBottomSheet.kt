package com.sliit.betterme.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.ChipGroup
import com.google.android.material.button.MaterialButton
import com.sliit.betterme.R
import androidx.fragment.app.activityViewModels

class AddHabitBottomSheet : BottomSheetDialogFragment() {
    private val viewModel: HabitsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sheet_add_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etName = view.findViewById<EditText>(R.id.etName)
        val chips = view.findViewById<ChipGroup>(R.id.chipGroupTime)
        val cgDays = view.findViewById<ChipGroup>(R.id.cgDays)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        // Icon quick picks and 'View all'
        var selectedIcon = "📱"
        var selectedIconViewId: Int = R.id.tvIcon1
        val primaryIcons = listOf(
            R.id.tvIcon1 to "📱",
            R.id.tvIcon2 to "⏰",
            R.id.tvIcon3 to "💧",
            R.id.tvIcon4 to "🏃"
        )
        val moreIconsRow = view.findViewById<View>(R.id.llMoreIcons)
        val moreIconsRow2 = view.findViewById<View>(R.id.llMoreIcons2)
        val moreIconsRow3 = view.findViewById<View>(R.id.llMoreIcons3)
        val moreIcons = listOf(
            R.id.tvIcon5 to "📚",
            R.id.tvIcon6 to "🧘",
            R.id.tvIcon7 to "🍎",
            R.id.tvIcon8 to "📝"
        )
        val moreIcons2 = listOf(
            R.id.tvIcon9 to "🏋️",
            R.id.tvIcon10 to "🚴",
            R.id.tvIcon11 to "🏊",
            R.id.tvIcon12 to "🪥",
            R.id.tvIcon13 to "🧹",
            R.id.tvIcon14 to "🛏️",
        )
        val moreIcons3 = listOf(
            R.id.tvIcon15 to "🧑‍🍳",
            R.id.tvIcon16 to "🧑‍💻",
            R.id.tvIcon17 to "🎯",
            R.id.tvIcon18 to "🧠",
            R.id.tvIcon19 to "🧑‍🎨",
            R.id.tvIcon20 to "📖",
        )
        fun updateIconHighlight(selectedId: Int) {
            val allIconIds = listOf(
                R.id.tvIcon1,R.id.tvIcon2,R.id.tvIcon3,R.id.tvIcon4,
                R.id.tvIcon5,R.id.tvIcon6,R.id.tvIcon7,R.id.tvIcon8,
                R.id.tvIcon9,R.id.tvIcon10,R.id.tvIcon11,R.id.tvIcon12,
                R.id.tvIcon13,R.id.tvIcon14,R.id.tvIcon15,R.id.tvIcon16,
                R.id.tvIcon17,R.id.tvIcon18,R.id.tvIcon19,R.id.tvIcon20
            )
            allIconIds.forEach { id ->
                view.findViewById<View>(id)?.setBackgroundResource(
                    if (id == selectedId) R.drawable.bg_icon_selected else R.drawable.bg_icon_unselect
                )
            }
        }
        fun attachClick(id: Int, value: String) {
            view.findViewById<View>(id)?.setOnClickListener {
                selectedIcon = value
                selectedIconViewId = id
                updateIconHighlight(id)
            }
        }
        primaryIcons.forEach { attachClick(it.first, it.second) }
        moreIcons.forEach { attachClick(it.first, it.second) }
        moreIcons2.forEach { attachClick(it.first, it.second) }
        moreIcons3.forEach { attachClick(it.first, it.second) }
        view.findViewById<View>(R.id.tvViewAllIcons)?.setOnClickListener {
            moreIconsRow.visibility = View.VISIBLE
            moreIconsRow2.visibility = View.VISIBLE
            moreIconsRow3.visibility = View.VISIBLE
        }
        // initialize icon highlight on default
        updateIconHighlight(selectedIconViewId)

        // Pastel color selection (simple alpha highlight)
        var selectedColorHex = "#CFF8E6" // default mint
        val colorMap = listOf(
            R.id.color1 to "#FFD6E7",
            R.id.color2 to "#FFE6CC",
            R.id.color3 to "#FFF7B2",
            R.id.color4 to "#CFF8E6",
            R.id.color5 to "#CFEFF3",
            R.id.color6 to "#D6E9FF",
            R.id.color7 to "#E5D7FF",
            R.id.color8 to "#E8D6FF",
        )
        fun updateColorHighlight(selectedId: Int) {
            colorMap.forEach { (vid, _) ->
                view.findViewById<View>(vid)?.alpha = if (vid == selectedId) 1f else 0.5f
            }
        }
        colorMap.forEach { (vid, hex) ->
            view.findViewById<View>(vid)?.setOnClickListener {
                selectedColorHex = hex
                updateColorHighlight(vid)
            }
        }
        // initialize highlight default (mint)
        updateColorHighlight(R.id.color4)

        // ----- Edit mode: prefill from arguments if present -----
        val editId = arguments?.getLong("edit_id", -1L) ?: -1L
        if (editId != -1L) {
            // Title
            etName.setText(arguments?.getString("edit_title", "") ?: "")
            // Time of day
            when (arguments?.getString("edit_tod")) {
                TimeOfDay.MORNING.name -> chips.check(R.id.chipMorning)
                TimeOfDay.AFTERNOON.name -> chips.check(R.id.chipAfternoon)
                TimeOfDay.EVENING.name -> chips.check(R.id.chipEvening)
                TimeOfDay.NIGHT.name -> chips.check(R.id.chipNight)
            }
            // Icon
            val iconValue = arguments?.getString("edit_icon")
            if (!iconValue.isNullOrEmpty()) {
                val all = listOf(
                    R.id.tvIcon1 to "📱", R.id.tvIcon2 to "⏰", R.id.tvIcon3 to "💧", R.id.tvIcon4 to "🏃",
                    R.id.tvIcon5 to "📚", R.id.tvIcon6 to "🧘", R.id.tvIcon7 to "🍎", R.id.tvIcon8 to "📝",
                    R.id.tvIcon9 to "🏋️", R.id.tvIcon10 to "🚴", R.id.tvIcon11 to "🏊", R.id.tvIcon12 to "🪥",
                    R.id.tvIcon13 to "🧹", R.id.tvIcon14 to "🛏️", R.id.tvIcon15 to "🧑‍🍳", R.id.tvIcon16 to "🧑‍💻",
                    R.id.tvIcon17 to "🎯", R.id.tvIcon18 to "🧠", R.id.tvIcon19 to "🧑‍🎨", R.id.tvIcon20 to "📖"
                )
                // reveal extra rows so highlight is visible
                view.findViewById<View>(R.id.llMoreIcons)?.visibility = View.VISIBLE
                view.findViewById<View>(R.id.llMoreIcons2)?.visibility = View.VISIBLE
                view.findViewById<View>(R.id.llMoreIcons3)?.visibility = View.VISIBLE
                val match = all.firstOrNull { it.second == iconValue }
                if (match != null) {
                    selectedIcon = iconValue
                    selectedIconViewId = match.first
                    updateIconHighlight(selectedIconViewId)
                }
            }
            // Color
            val colorValue = arguments?.getString("edit_color")
            if (!colorValue.isNullOrEmpty()) {
                val match = colorMap.firstOrNull { it.second.equals(colorValue, ignoreCase = true) }
                if (match != null) {
                    selectedColorHex = match.second
                    updateColorHighlight(match.first)
                } else {
                    // If custom color, still use it (no highlight change)
                    selectedColorHex = colorValue
                }
            }
            // Button text
            btnSave.text = "Update"
        }
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) { dismiss(); return@setOnClickListener }
            val tod = when (chips.checkedChipId) {
                R.id.chipMorning -> TimeOfDay.MORNING
                R.id.chipAfternoon -> TimeOfDay.AFTERNOON
                R.id.chipEvening -> TimeOfDay.EVENING
                R.id.chipNight -> TimeOfDay.NIGHT
                else -> TimeOfDay.MORNING
            }
            if (editId != -1L) {
                viewModel.updateHabit(
                    Habit(
                        id = editId,
                        title = name,
                        timeOfDay = tod,
                        icon = selectedIcon,
                        colorHex = selectedColorHex
                    )
                )
            } else {
                viewModel.addHabit(
                    Habit(
                        title = name,
                        timeOfDay = tod,
                        icon = selectedIcon,
                        colorHex = selectedColorHex
                    )
                )
            }
            parentFragmentManager.setFragmentResult(
                "new_habit_tod",
                bundleOf("tod" to tod.name)
            )
            dismiss()
        }
    }
}
