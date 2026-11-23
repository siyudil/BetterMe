package com.sliit.betterme.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sliit.betterme.R

class WeeklyHabitsFragment : Fragment() {
    private val viewModel: HabitsViewModel by activityViewModels()
    private lateinit var adapter: WeeklyHabitsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weekly_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvWeekly)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = WeeklyHabitsAdapter()
        rv.adapter = adapter

        viewModel.habits.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
        }
    }
}
