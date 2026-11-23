package com.sliit.betterme.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.sliit.betterme.R

class OnboardingPagerAdapter(
    private val items: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingPagerAdapter.PageVH>() {

    data class OnboardingPage(
        val header: String,
        val title: String,
        val description: String,
        @DrawableRes val imageRes: Int
    )

    class PageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeader: TextView = itemView.findViewById(R.id.tvHeader)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val ivIllustration: ImageView = itemView.findViewById(R.id.ivIllustration)

        fun bind(item: OnboardingPage) {
            tvHeader.text = item.header
            tvTitle.text = item.title
            tvDescription.text = item.description
            ivIllustration.setImageResource(item.imageRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding_page, parent, false)
        return PageVH(view)
    }

    override fun onBindViewHolder(holder: PageVH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
