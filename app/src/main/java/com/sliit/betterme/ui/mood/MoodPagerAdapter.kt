package com.sliit.betterme.ui.mood

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MoodPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment = when(position){
        0 -> MoodAddFragment()
        1 -> MoodListFragment()
        else -> MoodReportFragment()
    }
}
