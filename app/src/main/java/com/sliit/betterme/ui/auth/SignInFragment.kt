package com.sliit.betterme.ui.auth

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sliit.betterme.R
import com.sliit.betterme.util.Prefs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.util.Patterns

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val cbRemember = view.findViewById<CheckBox>(R.id.cbRemember)
        val tilEmail = view.findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword = view.findViewById<TextInputLayout>(R.id.tilPassword)

        // Initialize from preferences
        cbRemember.isChecked = Prefs.isRememberMe(requireContext())
        if (cbRemember.isChecked) {
            etEmail.setText(Prefs.getEmail(requireContext()))
        }

        view.findViewById<View>(R.id.btnLogin).setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = view.findViewById<TextInputEditText>(R.id.etPassword).text?.toString().orEmpty()

            // Reset previous errors
            tilEmail.error = null
            tilPassword.error = null

            var valid = true
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = getString(R.string.error_email_invalid)
                valid = false
            }
            val hasLetter = password.any { it.isLetter() }
            val hasDigit = password.any { it.isDigit() }
            if (password.length < 6 || !hasLetter || !hasDigit) {
                tilPassword.error = getString(R.string.error_password_requirements)
                valid = false
            }

            if (!valid) return@setOnClickListener

            // Validate against stored registered credentials
            val regEmail = Prefs.getRegisteredEmail(requireContext())
            val regPassword = Prefs.getRegisteredPassword(requireContext())
            if (email != regEmail || password != regPassword) {
                tilPassword.error = getString(R.string.error_invalid_credentials)
                return@setOnClickListener
            }

            // Persist preferences based on checkbox
            if (cbRemember.isChecked) {
                Prefs.setRememberMe(requireContext(), true)
                Prefs.saveEmail(requireContext(), email)
            } else {
                Prefs.setRememberMe(requireContext(), false)
                Prefs.clearEmail(requireContext())
            }
            findNavController().navigate(R.id.action_signIn_to_home)
        }

        val tvSignUp = view.findViewById<TextView>(R.id.tvSignUp)
        val fullText = tvSignUp.text.toString()
        val target = "Sign up"
        val start = fullText.indexOf(target)
        if (start >= 0) {
            val end = start + target.length
            val ssb = SpannableStringBuilder(fullText)

            // Clickable navigation only on "Sign up"
            val clickable = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().navigate(R.id.action_signIn_to_signUp)
                }
                override fun updateDrawState(ds: android.text.TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            ssb.setSpan(clickable, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Dark blue color for the word only
            val color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            ssb.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            tvSignUp.text = ssb
            tvSignUp.movementMethod = LinkMovementMethod.getInstance()
            tvSignUp.highlightColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }
}
