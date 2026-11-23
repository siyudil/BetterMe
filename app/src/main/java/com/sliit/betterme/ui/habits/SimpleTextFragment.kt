package com.sliit.betterme.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class SimpleTextFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val tv = TextView(requireContext())
        tv.textSize = 18f
        tv.text = requireArguments().getString(ARG_TEXT, "")
        tv.setPadding(32, 32, 32, 32)
        return tv
    }

    companion object {
        private const val ARG_TEXT = "text"
        fun newInstance(text: String) = SimpleTextFragment().apply {
            arguments = Bundle().apply { putString(ARG_TEXT, text) }
        }
    }
}
