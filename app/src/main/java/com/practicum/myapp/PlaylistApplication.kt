package com.practicum.myapp

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class PlaylistApplication : Application() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences("app_settings", MODE_PRIVATE)

        // Загружаем сохраненный режим темы и применяем его
        val isDarkThemeEnabled = sharedPrefs.getBoolean("dark_theme_enabled", false)
        if (isDarkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}