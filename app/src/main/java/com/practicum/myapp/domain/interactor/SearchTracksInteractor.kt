package com.practicum.myapp.domain.interactor

import com.practicum.myapp.Track
import com.practicum.myapp.domain.repositories.TrackRepository

/**
 * Интерактор для поиска треков по запросу.
 */
class SearchTracksInteractor(
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(query: String): List<Track> {
        return trackRepository.searchTracks(query)
    }
}
