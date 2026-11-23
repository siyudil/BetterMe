package com.sliit.betterme.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sliit.betterme.R
import com.sliit.betterme.util.Prefs
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment(R.layout.fragment_profile_page) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvBio = view.findViewById<TextView>(R.id.tvBio)

        tvName.text = Prefs.getRegisteredName(requireContext()).ifBlank { "Your Name" }
        tvEmail.text = Prefs.getRegisteredEmail(requireContext()).ifBlank { "" }
        val bioInitial = Prefs.getBio(requireContext())
        if (bioInitial.isNullOrBlank()) {
            tvBio.visibility = View.GONE
        } else {
            tvBio.text = bioInitial
            tvBio.visibility = View.VISIBLE
        }

        // Back arrow
        view.findViewById<View>(R.id.backBtn)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // Edit Profile: full name, email, optional new password
        view.findViewById<View>(R.id.rowEditProfile).setOnClickListener {
            val content = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
            val etFullName = content.findViewById<EditText>(R.id.etFullName)
            val etEmail = content.findViewById<EditText>(R.id.etEmail)
            val etBio = content.findViewById<EditText>(R.id.etBio)
            val etPassword = content.findViewById<EditText>(R.id.etPassword)

            etFullName.setText(Prefs.getRegisteredName(requireContext()))
            etEmail.setText(Prefs.getRegisteredEmail(requireContext()))
            etBio.setText(Prefs.getBio(requireContext()))
            etPassword.setText("") // blank if they want to change

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Profile")
                .setView(content)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save") { _, _ ->
                    val name = etFullName.text.toString().trim()
                    val email = etEmail.text.toString().trim()
                    val newPassword = etPassword.text.toString()
                    val bio = etBio.text.toString().trim()

                    Prefs.setRegisteredName(requireContext(), name)
                    if (newPassword.isNotEmpty()) {
                        Prefs.setRegisteredCredentials(requireContext(), email, newPassword)
                    } else {
                        // update email only
                        Prefs.setRegisteredCredentials(requireContext(), email, Prefs.getRegisteredPassword(requireContext()))
                    }

                    Prefs.setBio(requireContext(), bio)

                    tvName.text = name.ifBlank { "Your Name" }
                    tvEmail.text = email
                    if (bio.isBlank()) {
                        tvBio.visibility = View.GONE
                    } else {
                        tvBio.text = bio
                        tvBio.visibility = View.VISIBLE
                    }
                }
                .show()
        }

        // Settings navigation
        view.findViewById<View>(R.id.rowSettings).setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        // Logout -> Sign In
        view.findViewById<View>(R.id.rowLogout).setOnClickListener {
            findNavController().navigate(R.id.signInFragment)
        }

        // Delete Account -> clear prefs and go to Sign Up
        view.findViewById<View>(R.id.rowDeleteAccount).setOnClickListener {
            Prefs.clearAll(requireContext())
            findNavController().navigate(R.id.signUpFragment)
        }

        // Bottom navigation
        val bottom = view.findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.menu_profile
        bottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> { findNavController().navigate(R.id.homeFragment); true }
                R.id.menu_search -> { findNavController().navigate(R.id.searchFragment); true }
                R.id.menu_profile -> true
                else -> false
            }
        }
    }
}

