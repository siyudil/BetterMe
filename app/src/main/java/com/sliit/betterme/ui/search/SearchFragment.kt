package com.sliit.betterme.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sliit.betterme.R
import com.sliit.betterme.ui.hydration.HydrationEntry
import com.sliit.betterme.ui.hydration.HydrationViewModel
import androidx.navigation.fragment.findNavController

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val vm: HydrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Back
        view.findViewById<View>(R.id.backBtnSearch)?.setOnClickListener { findNavController().navigateUp() }

        // Page search suggestions
        val pages = listOf(
            PageItem("Home", R.id.homeFragment, R.drawable.ic_home),
            PageItem("Search", R.id.searchFragment, R.drawable.ic_search),
            PageItem("Profile", R.id.profileFragment, R.drawable.ic_user),
            PageItem("Settings", R.id.settingsFragment, R.drawable.ic_more_vert),
            PageItem("Habit Tracker", R.id.habitsFragment, R.drawable.ic_trophy),
            PageItem("Mood Journal", R.id.moodFragment, R.drawable.ic_search),
            PageItem("Hydration Reminder", R.id.hydrationFragment, R.drawable.ic_water)
        )
        val et = view.findViewById<EditText>(R.id.etQuery)
        val rv = view.findViewById<RecyclerView>(R.id.rvResults)
        val tvEmpty = view.findViewById<View>(R.id.tvEmpty)
        val adapter = SearchAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        fun doSearch(q: String){
            // First, page suggestions
            val pageMatches = if (q.isBlank()) emptyList() else pages.filter { it.title.contains(q, true) }
            if (pageMatches.isNotEmpty()) {
                // Show page suggestions as simple list items that navigate
                rv.adapter = PageAdapter(pageMatches) { destId ->
                    findNavController().navigate(destId)
                }
                tvEmpty.visibility = View.GONE
                return
            }

            // Otherwise, fallback to hydration search results
            val all = vm.getAllEntries()
            val list = if (q.isBlank()) emptyList() else all.filter {
                it.name.contains(q, true) || it.type.contains(q, true)
            }.sortedByDescending { it.timeMillis }
            rv.adapter = adapter
            adapter.submit(list)
            tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            if (q.isBlank()) (tvEmpty as TextView).text = "Type to search..." else (tvEmpty as TextView).text = "No results"
        }

        et.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { doSearch(s?.toString() ?: "") }
            override fun afterTextChanged(s: Editable?) {}
        })

        doSearch("")
    }
}

private class SearchAdapter: RecyclerView.Adapter<SearchVH>(){
    private val items = mutableListOf<HydrationEntry>()
    fun submit(list: List<HydrationEntry>){ items.clear(); items.addAll(list); notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_hydration_history, parent, false)
        return SearchVH(v)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: SearchVH, position: Int) = holder.bind(items[position])
}

private class SearchVH(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(e: HydrationEntry){
        (itemView.findViewById<TextView>(R.id.tvName)).text = e.name
        (itemView.findViewById<TextView>(R.id.tvAmount)).text = "${e.amountMl} mL"
        (itemView.findViewById<TextView>(R.id.tvTime)).text = android.text.format.DateFormat.format("MMM d, h:mm a", e.timeMillis)
        val icon = if (e.type.equals("water", true)) R.drawable.ic_water else R.drawable.ic_drink
        itemView.findViewById<android.widget.ImageView>(R.id.imgIcon).setImageResource(icon)
        // Hide menu in search results for simplicity
        itemView.findViewById<View>(R.id.btnMenu)?.visibility = View.GONE
    }
}

private data class PageItem(val title: String, val destId: Int, val iconRes: Int)

private class PageAdapter(
    private var items: List<PageItem>,
    private val onClick: (Int) -> Unit
): RecyclerView.Adapter<PageVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_search_page, parent, false)
        return PageVH(v, onClick)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: PageVH, position: Int) = holder.bind(items[position])
}

private class PageVH(itemView: View, val onClick: (Int) -> Unit): RecyclerView.ViewHolder(itemView){
    fun bind(item: PageItem){
        (itemView.findViewById<TextView>(R.id.tvTitle)).text = item.title
        itemView.findViewById<android.widget.ImageView>(R.id.imgIcon).setImageResource(item.iconRes)
        itemView.setOnClickListener { onClick(item.destId) }
    }
}
