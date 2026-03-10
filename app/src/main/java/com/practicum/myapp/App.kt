package com.practicum.myapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class App : Application() {
    
    var darkTheme: Boolean = false
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация HistoryManager
        HistoryManager.init(this)
        
        // Получаем SharedPreferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        
        // Получаем сохранённое значение темы (по умолчанию false - светлая тема)
        darkTheme = prefs.getBoolean("dark_theme_enabled", false)
        
        // Применяем тему
        applyTheme(darkTheme)
    }
    
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        
        // Сохраняем состояние темы в SharedPreferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putBoolean("dark_theme_enabled", darkThemeEnabled).apply()
        
        // Применяем тему
        applyTheme(darkThemeEnabled)
    }
    
    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun initializeTracks(): ArrayList<Track> {
        val tracks = ArrayList<Track>()

        tracks.add(Track(
            trackId = 1L,
            trackName = "Smells Like Teen Spirit",
            artistName = "Nirvana",
            trackTimeMillis = 301000L,
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ))

        tracks.add(Track(
            trackId = 2L,
            trackName = "Billie Jean",
            artistName = "Michael Jackson",
            trackTimeMillis = 295000L,
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ))

        tracks.add(Track(
            trackId = 3L,
            trackName = "Stayin' Alive",
            artistName = "Bee Gees",
            trackTimeMillis = 250000L,
            artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ))

        tracks.add(Track(
            trackId = 4L,
            trackName = "Whole Lotta Love",
            artistName = "Led Zeppelin",
            trackTimeMillis = 333000L,
            artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ))

        tracks.add(Track(
            trackId = 5L,
            trackName = "Sweet Child O'Mine",
            artistName = "Guns N' Roses",
            trackTimeMillis = 303000L,
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        ))

        return tracks
    }
}