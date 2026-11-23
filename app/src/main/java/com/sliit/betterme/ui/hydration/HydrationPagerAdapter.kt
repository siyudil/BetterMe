package com.sliit.betterme.ui.hydration

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HydrationPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment = when(position){
        0 -> TodayHydrationFragment()
        1 -> HistoryHydrationFragment()
        else -> ReportsHydrationFragment()
    }
}
