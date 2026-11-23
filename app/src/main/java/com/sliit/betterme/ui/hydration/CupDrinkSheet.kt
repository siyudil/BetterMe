package com.sliit.betterme.ui.hydration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.sliit.betterme.R

class CupDrinkSheet : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sheet_cup_drink, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sizes = listOf(100,125,150,200,250,300,350,400,500,600)
        val rvSizes = view.findViewById<RecyclerView>(R.id.rvSizes)
        rvSizes.layoutManager = GridLayoutManager(requireContext(), 4)
        rvSizes.adapter = SimpleChipAdapter(sizes.map { ChipItem("${it} mL", it) }) {
            setFragmentResult(RESULT_KEY, Bundle().apply {
                putInt("cup_size", it.payload as Int)
            })
            dismiss()
        }

        val drinks = listOf("Water","Coffee","Tea","Juice","Sports","Soda","Smoothie","Chocolate","Beer","Wine")
        val rvDrinks = view.findViewById<RecyclerView>(R.id.rvDrinks)
        rvDrinks.layoutManager = GridLayoutManager(requireContext(), 4)
        rvDrinks.adapter = SimpleChipAdapter(drinks.map { ChipItem(it, it) }) {
            setFragmentResult(RESULT_KEY, Bundle().apply {
                putString("drink_type", it.payload as String)
            })
            dismiss()
        }
    }

    data class ChipItem(val label: String, val payload: Any)

    class SimpleChipAdapter(private val items: List<ChipItem>, private val onClick: (ChipItem)->Unit) : RecyclerView.Adapter<VH>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chip_circle, parent, false)
            return VH(v, onClick)
        }
        override fun getItemCount(): Int = items.size
        override fun onBindViewHolder(holder: VH, position: Int) { holder.bind(items[position]) }
    }

    class VH(itemView: View, private val onClick: (ChipItem)->Unit) : RecyclerView.ViewHolder(itemView){
        private var item: ChipItem? = null
        init { itemView.setOnClickListener { item?.let(onClick) } }
        fun bind(i: ChipItem){
            item = i
            itemView.findViewById<TextView>(R.id.tvLabel).text = i.label
        }
    }

    companion object { const val RESULT_KEY = "cup_drink_result" }
}
