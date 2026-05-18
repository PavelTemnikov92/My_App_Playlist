package com.practicum.myapp.domain.repositories

import com.practicum.myapp.domain.model.Track
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для получения треков из источника данных.
 */
interface TrackRepository {
    /**
     * Поиск треков по запросу.
     */
    suspend fun searchTracks(query: String): List<Track>

    /**
     * Получить поток всех доступных треков (например, из кэша).
     */
    fun getAllTracks(): Flow<List<Track>>
}
