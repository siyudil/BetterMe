package com.sliit.betterme.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sliit.betterme.R

class Onboarding1Fragment : Fragment(R.layout.fragment_onboarding1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSkip = view.findViewById<View>(R.id.btnSkip)
        val btnNext = view.findViewById<View>(R.id.btnNext)

        btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_onboarding1_to_signUp)
        }

        btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_onboarding1_to_onboarding2)
        }
    }
}
