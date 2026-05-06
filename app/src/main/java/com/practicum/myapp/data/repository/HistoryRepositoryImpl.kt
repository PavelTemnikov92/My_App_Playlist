package com.practicum.myapp.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.myapp.Track
import com.practicum.myapp.domain.repositories.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Реализация HistoryRepository, сохраняющая историю в SharedPreferences в виде JSON.
 */
class HistoryRepositoryImpl(app: Application) : HistoryRepository {
    private val prefs: SharedPreferences =
        app.getSharedPreferences("history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val _history = MutableStateFlow<List<Track>>(loadHistory())

    private fun loadHistory(): List<Track> {
        val json = prefs.getString("history", null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveHistory(list: List<Track>) {
        val json = gson.toJson(list)
        prefs.edit().putString("history", json).apply()
    }

    override suspend fun addTrack(track: Track) {
        val updated = _history.value.toMutableList().apply { add(track) }
        _history.value = updated
        saveHistory(updated)
    }

    override suspend fun clearHistory() {
        _history.value = emptyList()
        saveHistory(emptyList())
    }

    override fun getHistory(): Flow<List<Track>> = _history.asStateFlow()
}
