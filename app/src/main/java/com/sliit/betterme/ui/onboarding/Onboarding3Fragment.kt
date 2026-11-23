package com.sliit.betterme.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sliit.betterme.R

class Onboarding3Fragment : Fragment(R.layout.fragment_onboarding3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.btnSkip).setOnClickListener {
            findNavController().navigate(R.id.action_onboarding3_to_signUp)
        }
        view.findViewById<View>(R.id.btnNext).setOnClickListener {
            findNavController().navigate(R.id.action_onboarding3_to_signUp)
        }
    }
}
