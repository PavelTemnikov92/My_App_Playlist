package com.practicum.myapp.data.repository

import com.practicum.myapp.Track as ApiTrack
import com.practicum.myapp.domain.model.Track
import com.practicum.myapp.domain.repositories.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Пример реализации репозитория треков, использующий простой in‑memory кэш.
 * В реальном проекте здесь будет Retrofit‑клиент для iTunes API.
 */
class TrackRepositoryImpl : TrackRepository {
    private val cache = MutableStateFlow<List<ApiTrack>>(emptyList())

    override suspend fun searchTracks(query: String): List<Track> {
        // TODO: заменить на реальный запрос к API
        val apiTracks = listOf(
            ApiTrack(1L, "Track $query", "Artist", 180_000L, null, null, null, null, null, null)
        )
        cache.value = apiTracks
        
        // Convert API Track to domain Track
        return apiTracks.map { apiTrack ->
            Track(
                trackId = apiTrack.trackId,
                trackName = apiTrack.trackName,
                artistName = apiTrack.artistName,
                trackTimeMillis = apiTrack.trackTimeMillis,
                artworkUrl100 = apiTrack.artworkUrl100,
                collectionName = apiTrack.collectionName,
                releaseDate = apiTrack.releaseDate,
                primaryGenreName = apiTrack.primaryGenreName,
                country = apiTrack.country,
                previewUrl = apiTrack.previewUrl
            )
        }
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return cache.map { apiTracks ->
            apiTracks.map { apiTrack ->
                Track(
                    trackId = apiTrack.trackId,
                    trackName = apiTrack.trackName,
                    artistName = apiTrack.artistName,
                    trackTimeMillis = apiTrack.trackTimeMillis,
                    artworkUrl100 = apiTrack.artworkUrl100,
                    collectionName = apiTrack.collectionName,
                    releaseDate = apiTrack.releaseDate,
                    primaryGenreName = apiTrack.primaryGenreName,
                    country = apiTrack.country,
                    previewUrl = apiTrack.previewUrl
                )
            }
        }
    }
}
