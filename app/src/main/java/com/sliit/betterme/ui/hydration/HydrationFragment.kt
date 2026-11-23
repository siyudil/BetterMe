package com.sliit.betterme.ui.hydration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sliit.betterme.R
import androidx.navigation.fragment.findNavController

class HydrationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.backBtnHydration)?.setOnClickListener {
            findNavController().navigateUp()
        }
        val pager = view.findViewById<ViewPager2>(R.id.viewPager)
        val tabs = view.findViewById<TabLayout>(R.id.tabLayout)
        pager.adapter = HydrationPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        val titles = listOf("Today", "History", "Reports")
        TabLayoutMediator(tabs, pager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}

