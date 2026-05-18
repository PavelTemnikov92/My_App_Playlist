package com.practicum.myapp.domain.repositories

import com.practicum.myapp.domain.model.HistoryTrack
import com.practicum.myapp.domain.model.Track

/**
 * Репозиторий для хранения и получения истории прослушиваний.
 */
interface HistoryRepository {
    /**
     * Добавить трек в историю.
     */
    fun addTrack(track: Track)

    /**
     * Очистить всю историю.
     */
    fun clearHistory()

    /**
     * Получить текущую историю.
     */
    fun getHistory(): List<HistoryTrack>
}
