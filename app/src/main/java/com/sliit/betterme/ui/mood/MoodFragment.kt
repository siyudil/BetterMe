package com.sliit.betterme.ui.mood

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sliit.betterme.R
import androidx.navigation.fragment.findNavController

class MoodFragment : Fragment(R.layout.fragment_mood) {
    private val viewModel: MoodViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.backBtnMood)?.setOnClickListener { findNavController().navigateUp() }
        val pager = view.findViewById<ViewPager2>(R.id.moodPager)
        val tabs = view.findViewById<TabLayout>(R.id.moodTabs)
        pager.adapter = MoodPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        val titles = listOf("Add", "List", "Report")
        TabLayoutMediator(tabs, pager) { tab, pos -> tab.text = titles[pos] }.attach()

        // Load persisted moods once
        viewModel.load()

        // When a mood is saved, switch to List tab
        parentFragmentManager.setFragmentResultListener("mood_saved", viewLifecycleOwner) { _, _ ->
            pager.currentItem = 1
        }
        parentFragmentManager.setFragmentResultListener("mood_edit_request", viewLifecycleOwner) { _, _ ->
            pager.currentItem = 0
        }
    }
}
