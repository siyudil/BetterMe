package com.sliit.betterme.ui.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sliit.betterme.R
import com.sliit.betterme.util.Prefs

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Greeting
        val name = Prefs.getRegisteredName(requireContext()).ifBlank { "there" }
        view.findViewById<TextView>(R.id.tvGreeting).text = "Hello, $name!"
        view.findViewById<TextView>(R.id.tvGreeting).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_profile)
        }

        // Cards
        view.findViewById<View>(R.id.cardHabits).apply {
            visibility = View.VISIBLE
            setOnClickListener {
                findNavController().navigate(R.id.action_home_to_habits)
            }
        }
        view.findViewById<View>(R.id.cardMood).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_mood)
        }
        view.findViewById<View>(R.id.cardHydration).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_hydration)
        }
        view.findViewById<View>(R.id.cardSettings).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }

        // Sub-greeting quick search
        view.findViewById<TextView>(R.id.tvSubGreeting).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_search)
        }

        // Bottom navigation
        val bottom = view.findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.menu_home
        bottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> true // already here
                R.id.menu_search -> { findNavController().navigate(R.id.action_home_to_search); true }
                R.id.menu_profile -> { findNavController().navigate(R.id.action_home_to_profile); true }
                else -> false
            }
        }
    }
}
