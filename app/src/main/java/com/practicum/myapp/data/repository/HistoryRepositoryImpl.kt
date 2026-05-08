package com.practicum.myapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.myapp.domain.model.HistoryTrack
import com.practicum.myapp.domain.model.Track
import com.practicum.myapp.domain.repositories.HistoryRepository

/**
 * Реализация HistoryRepository, сохраняющая историю в SharedPreferences в виде JSON.
 */
class HistoryRepositoryImpl(context: Context) : HistoryRepository {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun loadHistory(): List<HistoryTrack> {
        val json = prefs.getString("history", null) ?: return emptyList()
        val type = object : TypeToken<List<HistoryTrack>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveHistory(list: List<HistoryTrack>) {
        val json = gson.toJson(list)
        prefs.edit().putString("history", json).apply()
    }

    override fun addTrack(track: Track) {
        val currentHistory = loadHistory().toMutableList()
        val historyTrack = HistoryTrack.fromTrack(track)
        // Удаляем если уже есть и добавляем в начало
        currentHistory.removeAll { it.trackId == historyTrack.trackId }
        currentHistory.add(0, historyTrack)
        saveHistory(currentHistory)
    }

    override fun clearHistory() {
        saveHistory(emptyList())
    }

    override fun getHistory(): List<HistoryTrack> = loadHistory()
}
