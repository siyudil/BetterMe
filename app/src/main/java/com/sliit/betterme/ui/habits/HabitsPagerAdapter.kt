package com.sliit.betterme.ui.habits

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HabitsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = 4
    override fun createFragment(position: Int): Fragment = when(position){
        0 -> TodayHabitsFragment()
        1 -> WeeklyHabitsFragment()
        2 -> OverallHabitsFragment()
        else -> ReportsFragment()
    }
}
