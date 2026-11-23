package com.sliit.betterme.ui.auth

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sliit.betterme.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.util.Patterns
import com.sliit.betterme.util.Prefs

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirm = view.findViewById<TextInputEditText>(R.id.etConfirm)

        val tilName = view.findViewById<TextInputLayout>(R.id.tilName)
        val tilEmail = view.findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword = view.findViewById<TextInputLayout>(R.id.tilPassword)
        val tilConfirm = view.findViewById<TextInputLayout>(R.id.tilConfirm)

        view.findViewById<View>(R.id.btnSignUp).setOnClickListener {
            tilName.error = null
            tilEmail.error = null
            tilPassword.error = null
            tilConfirm.error = null

            val name = etName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()
            val confirm = etConfirm.text?.toString().orEmpty()

            var valid = true
            val nameRegex = Regex("^[A-Za-z ]+$")
            if (name.isEmpty() || !nameRegex.matches(name)) {
                tilName.error = getString(R.string.error_name_letters_only)
                valid = false
            }
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
            if (confirm != password) {
                tilConfirm.error = getString(R.string.error_password_mismatch)
                valid = false
            }

            if (!valid) return@setOnClickListener

            // Save registered credentials and name for simple local auth and greeting
            Prefs.setRegisteredCredentials(requireContext(), email, password)
            Prefs.setRegisteredName(requireContext(), name)

            findNavController().navigate(R.id.action_signUp_to_signIn)
        }

        val tvSignIn = view.findViewById<TextView>(R.id.tvSignIn)
        val fullText = tvSignIn.text?.toString() ?: ""
        // Find "sign in" ignoring case and extra spaces
        val regex = Regex("sign\\s+in", RegexOption.IGNORE_CASE)
        val match = regex.find(fullText)
        if (match != null) {
            val start = match.range.first
            val end = match.range.last + 1
            val ssb = SpannableStringBuilder(fullText)

            val clickable = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().navigate(R.id.action_signUp_to_signIn)
                }
                override fun updateDrawState(ds: android.text.TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            ssb.setSpan(clickable, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            ssb.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            tvSignIn.text = ssb
            tvSignIn.movementMethod = LinkMovementMethod.getInstance()
            tvSignIn.highlightColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }
}
