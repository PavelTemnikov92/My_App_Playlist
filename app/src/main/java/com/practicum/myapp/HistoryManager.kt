package com.practicum.myapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object
HistoryManager {
    private const val PREFS_NAME = "search_history"
    private const val KEY_HISTORY = "history_tracks"
    private const val MAX_HISTORY_SIZE = 10
    
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    
    // Инициализация менеджера
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // Получение истории
    fun getHistory(): List<HistoryTrack> {
        val json = prefs.getString(KEY_HISTORY, null)
        return if (json != null) {
            val type = object : TypeToken<List<HistoryTrack>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    
    // Добавление трека в историю
    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        // Создаём HistoryTrack из Track
        val historyTrack = HistoryTrack.fromTrack(track)

        // Удаляем существующую запись с таким же trackId или (если trackId = 0) по названию и исполнителю
        if (historyTrack.trackId != 0L) {
            history.removeAll { it.trackId == historyTrack.trackId }
        } else {
            history.removeAll { 
                it.trackName == historyTrack.trackName && it.artistName == historyTrack.artistName 
            }
        }

        // Добавляем трек в начало списка
        history.add(0, historyTrack)

        // Ограничиваем размер истории
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }

        // Сохраняем историю
        saveHistory(history)
    }
    
    // Очистка истории
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
    
    // Сохранение истории
    private fun saveHistory(history: List<HistoryTrack>) {
        val json = gson.toJson(history)
        prefs.edit().putString(KEY_HISTORY, json).apply()
    }
}
