package com.sliit.betterme.util

import android.content.Context
import android.content.Context.MODE_PRIVATE

object Prefs {
    private const val FILE = "betterme_prefs"
    private const val KEY_REMEMBER = "remember_me"
    private const val KEY_EMAIL = "remember_email"
    private const val KEY_REG_EMAIL = "registered_email"
    private const val KEY_REG_PASSWORD = "registered_password"
    private const val KEY_REG_NAME = "registered_name"
    private const val KEY_BIO = "user_bio"

    fun setRememberMe(context: Context, remember: Boolean) {
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_REMEMBER, remember)
            .apply()
    }

    fun isRememberMe(context: Context): Boolean =
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .getBoolean(KEY_REMEMBER, false)

    fun saveEmail(context: Context, email: String) {
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .edit()
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getEmail(context: Context): String =
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .getString(KEY_EMAIL, "") ?: ""

    fun clearEmail(context: Context) {
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .edit()
            .remove(KEY_EMAIL)
            .apply()
    }

    fun setRegisteredCredentials(context: Context, email: String, password: String) {
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .edit()
            .putString(KEY_REG_EMAIL, email)
            .putString(KEY_REG_PASSWORD, password)
            .apply()
    }

    fun getRegisteredEmail(context: Context): String =
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .getString(KEY_REG_EMAIL, "") ?: ""

    fun getRegisteredPassword(context: Context): String =
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .getString(KEY_REG_PASSWORD, "") ?: ""

    fun setRegisteredName(context: Context, name: String) {
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .edit()
            .putString(KEY_REG_NAME, name)
            .apply()
    }

    fun getRegisteredName(context: Context): String =
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .getString(KEY_REG_NAME, "") ?: ""

    fun setBio(context: Context, bio: String) {
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .edit()
            .putString(KEY_BIO, bio)
            .apply()
    }

    fun getBio(context: Context): String =
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .getString(KEY_BIO, "") ?: ""

    fun clearAll(context: Context) {
        context.getSharedPreferences(FILE, MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
