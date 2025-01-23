package com.foodiehub

import android.content.Context

object UserPreferences {
    private const val PREFS_NAME = "user_prefs"
    private const val PREF_USER_LOGGED_IN = "user_logged_in"
    private const val PREF_USER_NAME = "user_name"

    fun saveUserLoggedIn(context: Context, isLoggedIn: Boolean, username: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(PREF_USER_LOGGED_IN, isLoggedIn)
            putString(PREF_USER_NAME, username)
            apply()
        }
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_USER_LOGGED_IN, false)
    }

    fun getUserName(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PREF_USER_NAME, null)
    }
}
