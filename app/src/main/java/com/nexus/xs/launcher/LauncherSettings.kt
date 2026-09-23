package com.nexus.xs.launcher

import android.content.Context

object LauncherSettings {
    private const val PREFS = "nexus_launcher"

    fun isDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean("dark_mode", false)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean("dark_mode", enabled)
            .apply()
    }
}
