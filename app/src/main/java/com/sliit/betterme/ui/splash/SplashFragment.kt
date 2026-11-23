package com.sliit.betterme.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sliit.betterme.R
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.ViewCompat

class SplashFragment : Fragment(R.layout.fragment_splash) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, sys.top, 0, sys.bottom)
            insets
        }
        view.findViewById<View>(R.id.btnGetStarted).setOnClickListener {
            findNavController().navigate(R.id.action_splash_to_onboarding1)
        }
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        view?.let { v ->
            WindowInsetsControllerCompat(window, v).isAppearanceLightStatusBars = true
        }
    }
}
