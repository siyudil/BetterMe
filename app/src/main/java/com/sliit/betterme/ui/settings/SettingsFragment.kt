package com.sliit.betterme.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.sliit.betterme.R
import com.sliit.betterme.data.Prefs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.TextView

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefsView = super.onCreateView(inflater, container, savedInstanceState)

        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val padding = 16.dp
        root.setPadding(padding, padding, padding, padding)

        val header = LinearLayout(requireContext())
        header.orientation = LinearLayout.HORIZONTAL
        header.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        header.gravity = android.view.Gravity.CENTER_VERTICAL

        val back = ImageView(requireContext())
        back.id = R.id.backBtnSettings
        back.setImageResource(R.drawable.ic_arrow_back)
        header.addView(back, LinearLayout.LayoutParams(24.dp, 24.dp))

        val title = TextView(requireContext())
        title.text = "Settings"
        val titleLp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        titleLp.marginStart = 12.dp
        header.addView(title, titleLp)

        val contentLp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        contentLp.weight = 1f
        root.addView(header)
        root.addView(prefsView, contentLp)

        val bottom = BottomNavigationView(requireContext())
        bottom.id = R.id.bottomNav
        bottom.setBackgroundColor(android.graphics.Color.parseColor("#222A33"))
        bottom.inflateMenu(R.menu.menu_bottom_nav)
        root.addView(bottom, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        return root
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        val prefs = Prefs(requireContext())

        val intervalPref = findPreference<ListPreference>("hydration_interval")
        intervalPref?.setOnPreferenceChangeListener { _, newValue ->
            prefs.putString("hydration_interval", newValue.toString())
            true
        }

        val notificationsPref = findPreference<Preference>("pref_notifications")
        notificationsPref?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.notificationsFragment)
            true
        }

        val darkModePref = findPreference<SwitchPreferenceCompat>("pref_dark_mode")
        darkModePref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as? Boolean ?: false
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            true
        }

        val logoutPref = findPreference<Preference>("pref_logout")
        logoutPref?.setOnPreferenceClickListener {
            val options = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build()
            findNavController().navigate(R.id.signUpFragment, null, options)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.backBtnSettings)?.setOnClickListener {
            findNavController().navigateUp()
        }
        val bottom = view.findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.menu_home
        bottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> { findNavController().navigate(R.id.homeFragment); true }
                R.id.menu_search -> { findNavController().navigate(R.id.searchFragment); true }
                R.id.menu_profile -> { findNavController().navigate(R.id.profileFragment); true }
                else -> false
            }
        }
    }
}

private val Int.dp: Int get() = (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()

