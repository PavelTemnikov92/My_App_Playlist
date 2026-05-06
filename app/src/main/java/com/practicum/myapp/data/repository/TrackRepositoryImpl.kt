package com.practicum.myapp.data.repository

import com.practicum.myapp.Track
import com.practicum.myapp.domain.repositories.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Пример реализации репозитория треков, использующий простой in‑memory кэш.
 * В реальном проекте здесь будет Retrofit‑клиент для iTunes API.
 */
class TrackRepositoryImpl : TrackRepository {
    private val cache = MutableStateFlow<List<Track>>(emptyList())

    override suspend fun searchTracks(query: String): List<Track> {
        // TODO: заменить на реальный запрос к API
        val dummy = listOf(
            Track(1L, "Track $query", "Artist", 180_000L, "")
        )
        cache.value = dummy
        return dummy
    }

    override fun getAllTracks(): Flow<List<Track>> = cache.asStateFlow()
}
