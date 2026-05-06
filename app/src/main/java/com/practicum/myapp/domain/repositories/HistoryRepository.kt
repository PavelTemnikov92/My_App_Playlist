package com.practicum.myapp.domain.repositories

import com.practicum.myapp.Track
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для хранения и получения истории прослушиваний.
 */
interface HistoryRepository {
    /**
     * Добавить трек в историю.
     */
    suspend fun addTrack(track: Track)

    /**
     * Очистить всю историю.
     */
    suspend fun clearHistory()

    /**
     * Получить поток текущей истории.
     */
    fun getHistory(): Flow<List<Track>>
}
