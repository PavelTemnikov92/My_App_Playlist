package com.practicum.myapp.domain.interactors

import com.practicum.myapp.domain.repositories.TrackRepository
import com.practicum.myapp.Track
import kotlinx.coroutines.flow.first

/**
 * Интерактор для поиска треков.
 */
class SearchTracksInteractor(private val trackRepository: TrackRepository) {
    suspend fun search(query: String): List<Track> =
        trackRepository.searchTracks(query)

    suspend fun getAll(): List<Track> =
        trackRepository.getAllTracks().first()
}
