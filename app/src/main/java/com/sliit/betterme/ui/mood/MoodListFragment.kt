package com.sliit.betterme.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sliit.betterme.R

class MoodListFragment : Fragment() {
    private val viewModel: MoodViewModel by activityViewModels()
    private lateinit var adapter: MoodAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mood_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvMoods)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = MoodAdapter(mutableListOf(),
            onEdit = { entry ->
                viewModel.setEditing(entry)
                parentFragmentManager.setFragmentResult("mood_edit_request", bundleOf())
            },
            onDelete = { entry ->
                viewModel.delete(entry.id)
            }
        )
        rv.adapter = adapter

        viewModel.moods.observe(viewLifecycleOwner) { list ->
            adapter.update(list)
        }
    }
}
