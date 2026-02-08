package com.practicum.myapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Инициализация SharedPreferences
        sharedPrefs = getSharedPreferences("app_settings", MODE_PRIVATE)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val shareAppButton = findViewById<Button>(R.id.btnShareApp)
        shareAppButton.setOnClickListener {
            shareApp()
        }

        val supportButton = findViewById<Button>(R.id.btnSupport)
        supportButton.setOnClickListener {
            openSupportEmail()
        }

        val userAgreementButton = findViewById<Button>(R.id.btnUserAgreement)
        userAgreementButton.setOnClickListener {
            openUserAgreement()
        }

        val switchDarkTheme = findViewById<android.widget.Switch>(R.id.switchDarkTheme)

        // Установка начального состояния переключателя на основе сохраненной настройки
        val isDarkThemeEnabled = sharedPrefs.getBoolean("dark_theme_enabled", false)
        switchDarkTheme.isChecked = isDarkThemeEnabled

        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            onDarkThemeChanged(isChecked)
        }
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Изучите курс по Android-разработке в Практикуме: https://practicum.yandex.ru/android-developer/"
            )
            putExtra(Intent.EXTRA_SUBJECT, "Поделиться приложением")
        }
        val chooser = Intent.createChooser(shareIntent, "Поделиться через")
        startActivity(chooser)
    }

    private fun openSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = android.net.Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("pavel.temnikov@mail.ru"))
            putExtra(Intent.EXTRA_SUBJECT, "Сообщение разработчикам и разработчицам приложения Playlist Maker")
            putExtra(Intent.EXTRA_TEXT, "Спасибо разработчикам и разработчицам за крутое приложение!")
        }
        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://yandex.ru/legal/practicum_offer/ru/")
        }
        startActivity(agreementIntent)
    }

    private fun onDarkThemeChanged(isChecked: Boolean) {
        // Сохраняем состояние темы в SharedPreferences
        sharedPrefs.edit {
            putBoolean("dark_theme_enabled", isChecked)
        }

        // Переключаем тему приложения
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Перезапускаем активность для применения новой темы
        recreate()
    }
}

